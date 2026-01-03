let stompClient = null;
let userIdx = null;
let userNickname = null;
let currentRoomIdx = null;

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

    // Use event delegation for dynamically added room items
    $("#chatRooms").on("click", ".list-group-item", function(e) {
        e.preventDefault();
        const roomIdx = $(this).data("room-idx");
        const roomName = $(this).data("room-name");
        connectToRoom(roomIdx, roomName);
    });
});

function createRoom() {
    const roomName = prompt("Enter the name for the new chat room:");
    if (roomName) {
        // Here you would typically make an API call to create the room
        console.log("Creating room: " + roomName);
        alert("Room creation functionality is not yet implemented.");
        // After successful API call, you would add the room to the list:
        // const newRoom = { roomIdx: new Date().getTime(), roomName: roomName };
        // addRoomToList(newRoom);
    }
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

    const titleH5 = $("<h5>").addClass("mb-1")
        .append($("<span>").text(roomTitle).css("margin-right", "5px"))

    if (room.unreadMessageCount > 0) {
        titleH5.append($("<span>").addClass("badge badge-danger badge-pill mr-2").text(room.unreadMessageCount))
    }

    const titleDiv = $("<div>")
        .append(titleH5)
        .append($("<small>").text(room.users.length + " participants"));

    const infoDiv = $("<div>").addClass("text-right");

    if (room.lastMessagedAt) {
        infoDiv.append($("<small>").text(new Date(room.lastMessagedAt).toLocaleTimeString()));
    }

    roomItem.append(titleDiv);
    roomItem.append(infoDiv);

    if (roomListContainer) {
        roomListContainer.append(roomItem);
    } else {
        // If a container isn't provided, find the existing one or create it.
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
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        currentRoomIdx = roomIdx;
        $("#chatRoomName").text(roomName);

        stompClient.subscribe('/sub/chat/room/' + currentRoomIdx, function (message) {
            showMessage(JSON.parse(message.body));
        });

        // Update UI
        $("#roomListColumn").removeClass("col-md-12").addClass("col-md-3");
        $("#chatAreaColumn").show();
        $("#backToInitialView").show();
        $("#messages").empty(); // Clear previous messages
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
    // Assuming the message object has a senderNickname and a message field
    const sender = message.senderNickname || 'Unknown';
    const msg = message.message || '';
    $("#messages").append("<div><strong>" + sender + ":</strong> " + msg + "</div>");
    // Scroll to the bottom of the messages panel
    $("#messages").scrollTop($("#messages")[0].scrollHeight);
}