const mongoose = require('mongoose')
const Schema = mongoose.Schema

const userSchema = new Schema({
    sender: {
        type: String,
        required: true
    },
    recipient: {
        type: String,
        required: true
    },
    messageContent: {
        type: String,
        required: true,
    },
    date: {
        type: Date,
        required: true
    }
})

const messageModel = mongoose.model('messages', userSchema)

exports = module.exports = {
    messageModel
}