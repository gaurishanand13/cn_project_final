const socket = io()
console.log(socket.id)
io.io("testing", (data) => {
    console.log(data)
})