const express = require('express')
const app = express()
const http = require('http')
const server = http.Server(app)
const io = require('socket.io')(server)


const bodyParser = require('body-parser');
app.use(bodyParser.urlencoded({
    extended: true
}));
app.use(bodyParser.json());


app.use('/', express.static(__dirname + "/public"))


io.on('connection', (socket) => {
    console.log(socket.id)
    socket.emit('testing', "mudneu")
    let userName = '' //This will be the username of the person from where this person logins , since we will be using it again and again. So let's make it global.

    socket.on('subscribe', (data) => {
        const json_data = JSON.parse(data)

        //Here we will be fetching the details of room and the username of the client which he has joined.
        userName = json_data.userName
        const roomName = json_data.roomName

        //Make the user join the room, so that when we send some message to room. He receives that message too.
        socket.json(roomName)

        //Also ask the app to update the adding of new user.
        io.to(roomName).emit('newUserToChatRoom', userName)
    })

    socket.on('unsubscribe', (data) => {
        const json_data = JSON.parse(data)

        //Here we will be fetching the details of room and the username of the client which he has joined.
        userName = json_data.userName
        const roomName = json_data.roomName

        //First ask the app to update the leaving of a user.
        io.to(roomName).emit('userLeftChatRoom', userName)

        //Therefore now remove this person with this socket from the rooom, so that whenever use sends a message to this room
        socket.leave(roomName) //this user doesn't receive the message.
    })


    //Handling the message when a user sends a data to the other use on chat.
    socket.on('newMessage', (data) => {
        const json_data = JSON.parse(data)


        const messageContent = json_data.messageContent
        const roomName = json_data.roomName

        const chatData = {
            userName: userName,
            messageContent: messageContent,
            roomName: roomName
        }
        io.to(roomName).emit('updateChat', JSON.stringify(chatData))
    })

    socket.on('disconnect', () => {
        console.log('User with id = ' + socket.id + " is disconnect")
    })
})

//

server.listen(process.env.PORT || 5000, () => {
    console.log('Started server on http://localhost:5000')
})