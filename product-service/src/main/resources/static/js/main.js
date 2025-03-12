var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
var usernameForm = document.querySelector('#usernameForm');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');
var roomIdSelect = document.querySelector('#roomId'); // Select element for room IDs

// Variables
var stompClient = null;
var username = null;
var recipientId = null;
var currentRoomId = null; // Default room

var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

// Connect to WebSocket
function connect(event) {
    event.preventDefault();
    username = document.querySelector('#name').value.trim();
    recipientId = document.querySelector('#recipientId').value.trim();
    currentRoomId = [username, recipientId].sort().join('-'); // Create a unique room ID for the private chat

    if (username && recipientId) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        var socket = new SockJS('/websocket');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    }
}

// Called when WebSocket is successfully connected
function onConnected() {
    // Subscribe to the private topic
    stompClient.subscribe('/topic/private/' + currentRoomId, onMessageReceived);

    // Tell the server about the user joining
    stompClient.send("/app/chat.addUser", {},
        JSON.stringify({ senderId: username, recipientId: recipientId, type: 'JOIN', roomId: currentRoomId })
    );

    connectingElement.classList.add('hidden');
    fetchChatHistory(currentRoomId);
    fetchUserRooms(username); // Fetch user rooms on connect
}

// Handle WebSocket connection errors
function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}

// Send a message
function sendMessage(event) {
    event.preventDefault();
    var messageContent = messageInput.value.trim();

    if (messageContent && stompClient) {
        // Fetch avatar and name before sending the message
        fetch(`/account/get-avatar/${username}`)
            .then(response => response.json())
            .then(data => {
                var chatMessage = {
                    senderId: username,
                    roomId: currentRoomId,
                    recipientId: recipientId,
                    message: messageContent,
                    type: 'CHAT',
                    avatar: data.avatar,
                    name: data.name || "concac"
                };
                stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
                messageInput.value = '';
            })
            .catch(error => {
                console.error('Error fetching avatar:', error);
            });
    }
}

// Handle incoming messages
function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);

    var messageElement = document.createElement('li');

    if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.message = message.sender + ' left!';
    } else {
        messageElement.classList.add('chat-message');

        // Avatar element
        var avatarElement = document.createElement('img');
        avatarElement.src = message.avatar; // Avatar URL
        avatarElement.classList.add('avatar-img'); // Add a class for styling
        messageElement.appendChild(avatarElement);
        // Username element
        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(message.name || 'concac'); // Display name
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    }

    var textElement = document.createElement('p');
    var messageText = document.createTextNode(message.message);
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}

// Generate a color based on the message sender
function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    var index = Math.abs(hash % colors.length);
    return colors[index];
}

// Fetch chat history for a room
function fetchChatHistory(roomId) {
    fetch(`/chat/history/${roomId}`)
        .then(response => response.json())
        .then(data => {
            messageArea.innerHTML = '';
            // Fetch avatar and name for each message
            const promises = data.map(message =>
                fetch(`/account/get-avatar/${message.senderId}`)
                    .then(response => response.json())
                    .then(avatarData => {
                        message.avatar = avatarData.avatar;
                        message.name = avatarData.name;
                        return message;
                    })
                    .catch(error => {
                        console.error('Error fetching avatar:', error);
                        message.avatar = ''; // Default or error avatar
                        message.name = 'Unknown'; // Default name
                        return message;
                    })
            );

            // Once all avatar and name data is fetched, display messages
            Promise.all(promises)
                .then(messages => {
                    messages.forEach(message => {
                        onMessageReceived({ body: JSON.stringify(message) });
                    });
                })
                .catch(error => {
                    console.error('Error processing messages:', error);
                });
        })
        .catch(error => {
            console.error('Error fetching chat history:', error);
        });
}

// Fetch available rooms for the user
function fetchUserRooms(username) {
    fetch(`/chat/rooms/${username}`)
        .then(response => response.json())
        .then(data => {
            roomIdSelect.innerHTML = ''; // Clear existing options
            data.forEach(room => {
                var option = document.createElement('option');
                option.value = room.roomId;
                option.textContent = `${room.nameSender}: ${room.newMessage}`; // Display sender's name and latest message
                roomIdSelect.appendChild(option);
            });
        })
        .catch(error => {
            console.error('Error fetching user rooms:', error);
        });
}

// Switch to a selected room
function switchRoom() {
    var selectedRoomId = roomIdSelect.value;
    if (selectedRoomId !== currentRoomId) {
        currentRoomId = selectedRoomId;
        stompClient.unsubscribe('/topic/private/' + currentRoomId);
        stompClient.subscribe('/topic/private/' + currentRoomId, onMessageReceived);
        fetchChatHistory(currentRoomId);
    }
}

// Event listeners
usernameForm.addEventListener('submit', connect, true);
messageForm.addEventListener('submit', sendMessage, true);
roomIdSelect.addEventListener('change', switchRoom, true);