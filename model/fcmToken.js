const mongoose = require('mongoose')
const Schema = mongoose.Schema

const fcmSchema = new Schema({
    email: {
        type: String,
        unique: true,
        required: true
    },
    fcmToken: {
        type: String,
        unique: true,
        required: true
    }
})

const fcmTokeneModel = mongoose.model('fcmToken', fcmSchema)

exports = module.exports = {
    fcmTokeneModel
}