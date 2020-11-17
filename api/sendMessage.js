const route = require('express').Router();
const { fcmTokeneModel } = require('./../model/fcmToken')
const { messageModel } = require('./../model/message')
const { auth } = require('./../middleware/auth')
const { sendMsgToUserWithFCMToken } = require('./../fcm/sendFCMMsg')
const { userModel } = require('./../model/user')


function saveMessageInDatabase() {

}

route.post('/', async(req, res) => {
    const error = new Error()
    const data = {
        message: "Success"
    }
    try {
        auth(req, res, async(req, res) => {
            const sendersEmail = req.user.email
            const receipentsEmail = req.body.email
            const message = req.body.message
                //Now first find the fcm token of the receipent from fcmTokeneModel
            console.log(await fcmTokeneModel.find({}))
            const user = await fcmTokeneModel.findOne({
                email: receipentsEmail
            })
            if (user) {
                //User exists, now send the message to the user and also save the message in your database
                await sendMsgToUserWithFCMToken(user.fcmToken, message, sendersEmail, receipentsEmail)

                //Saving the message in message table
                saveMessageInDatabase()
                console.log('here 1')
                res.status(200).send(data)
            } else {
                //Now it may happen that the user is not logged in, but has an account. Therefore search for the user in userModel Table
                const x = await userModel.findOne({
                    email: receipentsEmail
                })
                if (x) {
                    //User has previously made an account ,  but is not currently logged in. Therefore just
                    //save this message in message table.
                    saveMessageInDatabase()
                    console.log('here 2')
                    res.status(200).send(data)
                } else {
                    error.message = "User doesn't exists"
                    res.status(401).send(error)
                }
            }
        })
    } catch (err) {
        error.message = err.message
        res.status(401).send(error)
    }
})


exports = module.exports = {
    route
}