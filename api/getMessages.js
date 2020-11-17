const route = require('express').Router();
const { messageModel } = require('./../model/message')
const { auth } = require('./../middleware/auth')

route.post('/', async(req, res) => {

    const error = new Error()
    try {
        auth(req, res, async(req, res) => {
            const user = req.user
            const messages = await messageModel.find({
                $or: [{
                        sender: user.email
                    },
                    {
                        recipient: user.email
                    }
                ]
            }).sort({
                date: -1
            })
            const data = {
                allMessages: []
            }
            for (mgs in messages) {
                var date_x = mgs.date.getFullYear() + "-" + (mgs.date.getMonth() + 1) + "-" + mgs.date.getDate()
                var time_x = mgs.date.getHours() + "-" + mgs.date.getMinutes()
                let x = {
                    sender: mgs.sender,
                    recipient: mgs.recipient,
                    messageContent: mgs.messageContent,
                    date: date_x,
                    time: time_x
                }
                data.allMessages.push(x)
            }
            res.status(201).send(data)
        })
    } catch (err) {
        error.message = err.message
        res.status(401).send(error)
    }
})


exports = module.exports = {
    route
}