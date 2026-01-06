let stompClient = null;
let userIdx = null;
let userNickname = null;
let currentRoomIdx = null;
let selectedUser = null;

$(document).ready(function() {
    // Fetch user info first, then fetch chat rooms
    $.get("/user", function(data) {
        userIdx = data.idx;
        userNickname = data.nickName || data.name; // Use nickname, or name as fallback
        $("#userEmail").text(data.email);
        $("#userNickname").text(userNickname);
        fetchChatRoomsAndDisplay();
    }).fail(function() {
        alert("User not authenticated. Please log in.");
        // window.location.href = "/login"; // Optional: redirect to login
    });

    // Attach event listeners
    $("#send").on("click", sendMessage);
    $("#message").on("keypress", function(e) {
        if (e.which === 13) { // Enter key pressed
            sendMessage();
            return false; // Prevent default action (e.g., form submission)
        }
    });
    $("#hideChatBtn").on("click", backToInitialView);
    $("#leaveRoomBtn").on("click", backToInitialView);
    $("#backToInitialView").on("click", backToInitialView);
    $("#createRoomBtn").on("click", createRoom);
    $("#logoutBtn").on("click", () => { window.location.href = "/logout"; });
    $("#myPageBtn").on("click", () => { window.location.href = "/mypage"; });

    // Modal-related event listeners
    $("#user-search-btn").on("click", searchUsers);
    $("#back-to-search-btn").on("click", backToSearchStep);
    $("#create-room-confirm-btn").on("click", createRoomConfirm);

    $('#user-search-results').on('click', '.list-group-item', function() {
        selectedUser = {
            idx: $(this).data('user-idx'),
            name: $(this).data('user-name')
        };
        $("#user-search-step").hide();
        $("#room-title-step").show();
    });


    // Use event delegation for dynamically added room items
    $("#chatRooms").on("click", ".list-group-item", function(e) {
        e.preventDefault();
        const roomIdx = $(this).data("room-idx");
        const roomName = $(this).data("room-name");
        connectToRoom(roomIdx, roomName);
    });
});

function createRoom() {
    // Show the modal and reset it
    $('#createRoomModal').modal('show');
    backToSearchStep();
    $('#user-search-input-name').val('');
    $('#user-search-input-email').val('');
    $('#user-search-results').empty();
    $('#room-title-input').val('');
}

function backToSearchStep() {
    $("#room-title-step").hide();
    $("#user-search-step").show();
    selectedUser = null;
}

function searchUsers() {
    const name = $("#user-search-input-name").val();
    const email = $("#user-search-input-email").val();

    if (!name && !email) {
        return;
    }

    const searchData = {
        size: 10
    };

    if (name) {
        searchData.name = name;
    }
    if (email) {
        searchData.email = email;
    }

    $.get("/user/search", searchData)
        .done(function(response) {
            const resultsContainer = $("#user-search-results");
            resultsContainer.empty();
            if (response && response.users && response.users.content && response.users.content.length > 0) {
                const resultList = $("<div>").addClass("list-group");
                response.users.content.forEach(function(user) {
                    // Don't show the current user in the search results
                    if (user.idx === userIdx) {
                        return;
                    }
                    const userItem = $("<a>")
                        .addClass("list-group-item list-group-item-action")
                        .attr("href", "#")
                        .data("user-idx", user.idx)
                        .data("user-name", user.nickName || user.name)
                        .text((user.nickName || user.name) + ' (' + user.email + ')');
                    resultList.append(userItem);
                });
                resultsContainer.append(resultList);
            } else {
                resultsContainer.text("No users found.");
            }
        })
        .fail(function() {
            $("#user-search-results").text("Error searching for users.");
        });
}

function createRoomConfirm() {
    if (!selectedUser) {
        alert("Please select a user.");
        return;
    }

    const roomTitle = $("#room-title-input").val();

    const requestData = {
        title: roomTitle,
        invitedUserIdx: selectedUser.idx
    };

    $.ajax({
        url: "/chats/room",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(requestData),
        success: function(response) {
            $('#createRoomModal').modal('hide');
            fetchChatRoomsAndDisplay();
            // Optionally, connect to the new room immediately
            // connectToRoom(response.idx, response.title);
        },
        error: function() {
            alert("Error creating chat room.");
        }
    });
}

function fetchChatRoomsAndDisplay() {
    $.get("/chats/rooms", function(response) {
        $("#chatRooms").empty();
        if (response && response.rooms && response.rooms.content && response.rooms.content.length > 0) {
            const roomListContainer = $("<div>").addClass("list-group");
            response.rooms.content.forEach(function(room) {
                addRoomToList(room, roomListContainer);
            });
            $("#chatRooms").append(roomListContainer);
        } else {
            $("#chatRooms").append("<p>No chat rooms found.</p>");
        }
    }).fail(function() {
        $("#chatRooms").append("<p>Error loading chat rooms.</p>");
    });
}

function addRoomToList(room, roomListContainer) {
    const roomTitle = room.title || room.users.join(', ');
    const roomName = room.title || room.users.join(', ');
    const roomItem = $("<a>")
        .addClass("list-group-item list-group-item-action d-flex justify-content-between align-items-center")
        .attr("href", "#")
        .data("room-idx", room.idx)
        .data("room-name", roomName)
        .data("unread-count", room.unreadMessageCount); // Store unread count

    const titleAndParticipantsDiv = $("<div>");
    const titleH5 = $("<h5>").addClass("mb-1").text(roomTitle);
    if (room.unreadMessageCount > 0) {
        const badge = $("<span>")
            .addClass("badge badge-danger badge-pill ml-2")
            .attr("id", `badge-for-room-${room.idx}`) // Add unique ID
            .text(room.unreadMessageCount);
        titleH5.append(badge);
    }
    titleAndParticipantsDiv.append(titleH5);
    titleAndParticipantsDiv.append($("<small>").text(room.users.length + " participants"));

    const timeSmall = $("<small>");
    if (room.lastMessagedAt) {
        timeSmall.text(new Date(room.lastMessagedAt).toLocaleTimeString());
    }

    roomItem.append(titleAndParticipantsDiv);
    roomItem.append(timeSmall);


    if (roomListContainer) {
        roomListContainer.append(roomItem);
    } else {
        let container = $("#chatRooms .list-group");
        if (container.length === 0) {
            container = $("<div>").addClass("list-group");
            $("#chatRooms").append(container);
        }
        container.append(roomItem);
    }
}


function connectToRoom(roomIdx, roomName) {
    if (stompClient) {
        stompClient.deactivate();
    }
    $("#messages").empty();

    stompClient = new StompJs.Client({
        brokerURL: 'ws://' + window.location.host + '/stomp/chats',
        onConnect: (frame) => {
            console.log('Connected: ' + frame);
            currentRoomIdx = roomIdx;
            $("#chatRoomName").text(roomName);

            // Subscribe to the chat room
            stompClient.subscribe('/sub/chats/' + currentRoomIdx, (message) => {
                showMessage(JSON.parse(message.body));
            });

            // Fetch previous messages
            fetchPreviousMessages(currentRoomIdx);

            // Reset unread message count
            resetUnreadCount(currentRoomIdx);

            $("#roomListColumn").removeClass("col-md-12").addClass("col-md-3");
            $("#chatAreaColumn").show();
            $("#backToInitialView").show();
        },
        onStompError: (frame) => {
            console.error('Broker reported error: ' + frame.headers['message']);
            console.error('Additional details: ' + frame.body);
        }
    });

    stompClient.activate();
}

function resetUnreadCount(roomIdx) {
    $.ajax({
        url: "/chats/" + roomIdx,
        type: "PATCH",
        success: function() {
            // Update the data attribute for consistency
            const roomItem = $(`#chatRooms .list-group-item[data-room-idx="${roomIdx}"]`);
            if (roomItem.length) {
                roomItem.data("unread-count", 0);
            }
            
            // Remove the badge using its unique ID
            $(`#badge-for-room-${roomIdx}`).remove();
        },
        error: function(xhr, status, error) {
            console.error("Failed to reset unread count:", error);
        }
    });
}

function fetchPreviousMessages(roomIdx) {
    const messagesContainer = $("#messages");
    messagesContainer.empty(); // Clear previous messages

    $.get(`/chats/${roomIdx}`)
        .done(function(response) {
            if (response && response.messages && response.messages.content) {
                // Messages are DESC, reverse to show oldest first
                const messages = response.messages.content.reverse();
                messages.forEach(function(message) {
                    showMessage(message);
                });
            }
        })
        .fail(function() {
            messagesContainer.append("<p>Could not fetch previous messages.</p>");
        });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.deactivate();
    }
    stompClient = null;
    currentRoomIdx = null;
    console.log("Disconnected");
}

function backToInitialView() {
    disconnect();
    $("#chatAreaColumn").hide();
    $("#roomListColumn").removeClass("col-md-3").addClass("col-md-12");
    $("#backToInitialView").hide();
}

function sendMessage() {
    const messageContent = $("#message").val();
    if (messageContent && stompClient && currentRoomIdx) {
        const chatMessage = {
            type: 'TEXT',
            message: messageContent
        };
        stompClient.publish({
            destination: "/pub/chats/" + currentRoomIdx,
            body: JSON.stringify(chatMessage)
        });
        $("#message").val("");
    }
}

function showMessage(message) {
    const messagesContainer = $("#messages");
    const isMyMessage = message.user && message.user.idx === userIdx;

    const alignment = isMyMessage ? 'right' : 'left';

    const messageContainer = $("<div>").addClass(`message-container ${alignment}`);
    const bubble = $("<div>").addClass(`message-bubble ${alignment}`);
    const sender = $("<div>").addClass('sender-name');
    const msg = $("<div>").text(message.message || '');

    if (!isMyMessage) {
        sender.text(message.user.nickName || message.user.name || 'Unknown');
        bubble.append(sender);
    }

    bubble.append(msg);
    messageContainer.append(bubble);
    messagesContainer.append(messageContainer);

    // Scroll to the bottom
    messagesContainer.scrollTop(messagesContainer[0].scrollHeight);
}