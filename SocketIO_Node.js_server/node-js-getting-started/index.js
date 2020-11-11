const express = require('express')
var app = express();
var http = require('http').Server(app);
var io = require('socket.io')(http);

app.set('port', (process.env.PORT || 3000));

const bodyParser = require('body-parser');
app.use(bodyParser.urlencoded({
    extended: true
}));
app.use(bodyParser.json());


console.log("outside io");

io.on('connection', function(socket) {

    console.log(socket.id)
    let userName = '' //This will be the username of the person from where this person logins , since we will be using it again and again. So let's make it global.

    socket.on('subscribe', (data) => {

        //Here we will be fetching the details of room and the username of the client which he has joined.
        userName = data.userName
        const roomName = data.roomName

        console.log('data in subsribe = ', userName, roomName)

        //Make the user join the room, so that when we send some message to room. He receives that message too.
        socket.join(roomName)

        const json = {
            userName: userName
        }

        //Also ask the app to update the adding of new user.
        io.to(roomName).emit('newUserToChatRoom', json)
    })

    socket.on('unsubscribe', (data) => {

        //Here we will be fetching the details of room and the username of the client which he has joined.
        userName = data.userName
        const roomName = data.roomName

        const json = {
            userName: userName
        }

        //First ask the app to update the leaving of a user.
        io.to(roomName).emit('userLeftChatRoom', json)

        //Therefore now remove this person with this socket from the rooom, so that whenever use sends a message to this room
        socket.leave(roomName) //this user doesn't receive the message.
    })


    //Handling the message when a user sends a data to the other use on chat.
    socket.on('newMessage', (data) => {
        const chatData = {
            userName: data.userName,
            messageContent: data.messageContent,
            roomName: data.roomName
        }
        console.log('Message Received', chatData)
        io.to(data.roomName).emit('updateChat', chatData)
    })

    socket.on('disconnect', () => {
        console.log('User with id = ' + socket.id + " is disconnect")
    })
});

http.listen(app.get('port'), function() {
    console.log('Node app is running on port', app.get('port'));
});