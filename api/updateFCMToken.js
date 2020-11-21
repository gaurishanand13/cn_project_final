const route = require('express').Router();
const { userModel } = require('./../model/user')
const { auth } = require('./../middleware/auth')

/**
 * We have to update the fcmToken in the userModel table, just make sure while updating user exists in table
 */
route.post('/', async(req, res) => {
    const error = new Error()
    try {
        auth(req, res, async(req, res) => {
            /**
             * Now insert the fcm token of this email in database, first find if user exist in the database or not
             */
            const user = await userModel.findOne({
                email: req.user.email
            })
            if (user) {
                //It means user exists in the database, so we can update the database
                console.log('SUCCESS IN fcmTOKEN', req.body.fcmToken)
                await userModel.updateOne({
                    email: user.email
                }, {
                    fcmToken: req.body.fcmToken
                })
            } else {
                //User doesn't exists in the database, so don't need to add the fcm token anywhere.
                console.log('user doesnt exists')
            }
            const data = {
                message: "success"
            }
            console.log('SUCCESS IN fcmTOKEN', req.body.fcmToken)
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