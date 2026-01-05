let stompClient = null;
let userIdx = null;
let userNickname = null;
let currentRoomIdx = null;
let selectedUser = null;

$(document).ready(function() {
    // Fetch user info first, then fetch chat rooms
    $.get("/user", function(data) {
        userIdx = data.userIdx;
        userNickname = data.nickname || data.name; // Use nickname, or name as fallback
        $("#userEmail").text(data.email);
        $("#userNickname").text(userNickname);
        fetchChatRoomsAndDisplay();
    }).fail(function() {
        alert("User not authenticated. Please log in.");
        // window.location.href = "/login"; // Optional: redirect to login
    });

    // Attach event listeners
    $("#send").on("click", sendMessage);
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
                        .data("user-name", user.nickname || user.name)
                        .text((user.nickname || user.name) + ' (' + user.email + ')');
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
        .data("room-name", roomName);

    const titleAndParticipantsDiv = $("<div>");
    const titleH5 = $("<h5>").addClass("mb-1").text(roomTitle);
    if (room.unreadMessageCount > 0) {
        titleH5.append($("<span>").addClass("badge badge-danger badge-pill ml-2").text(room.unreadMessageCount));
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
        stompClient.disconnect(() => {
            console.log("Disconnected from previous room.");
        });
    }

    const socket = new SockJS('/stomp/chats');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);
        currentRoomIdx = roomIdx;
        $("#chatRoomName").text(roomName);

        stompClient.subscribe('/sub/chat/room/' + currentRoomIdx, function(message) {
            showMessage(JSON.parse(message.body));
        });

        $("#roomListColumn").removeClass("col-md-12").addClass("col-md-3");
        $("#chatAreaColumn").show();
        $("#backToInitialView").show();
        $("#messages").empty();
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
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
            'roomIdx': currentRoomIdx,
            'userIdx': userIdx,
            'message': messageContent
        };
        stompClient.send("/pub/chat/message", {}, JSON.stringify(chatMessage));
        $("#message").val("");
    }
}

function showMessage(message) {
    const sender = message.senderNickname || 'Unknown';
    const msg = message.message || '';
    $("#messages").append("<div><strong>" + sender + ":</strong> " + msg + "</div>");
    $("#messages").scrollTop($("#messages")[0].scrollHeight);
}