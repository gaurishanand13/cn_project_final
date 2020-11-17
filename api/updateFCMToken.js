const route = require('express').Router();
const { fcmTokeneModel } = require('./../model/fcmToken')
const { userModel } = require('./../model/user')
const { auth } = require('./../middleware/auth')

/**
 * Note FCM token table will be having the table only when the user is logged in, when user logsOut. The app should
 * backout.
 */
route.post('/', async(req, res) => {
    const error = new Error()
    try {
        auth(req, res, async(req, res) => {
            /**
             * Now insert the fcm token of this email in database, first find if user exist in the database or not
             */
            const user = await fcmTokeneModel.findOne({
                email: req.user.email
            })
            if (user) {
                //It means update the already inserted token of an exisiting email
                console.log('already has an existing email')
                await fcmTokeneModel.updateOne({
                    email: user.email
                }, {
                    fcmToken: req.body.fcmToken
                })
                console.log('hey1', req.body.fcmToken)
            } else {
                console.log('does not have an existing email');
                //It means the fcm token of the user is being inserted for the first time, so insert it
                const newToken = new fcmTokeneModel({
                    email: req.user.email,
                    fcmToken: req.body.fcmToken
                })
                await newToken.save()
            }
            const data = {
                message: "success"
            }
            console.log('success in saving token')
            res.status(200).send(data)
        })
    } catch (err) {
        console.log('error in saving token= ', err.message)
        error.message = err.message
        res.status(401).send(error)
    }
})


exports = module.exports = {
    route
}