const route = require('express').Router();
const { fcmTokeneModel } = require('./../model/fcmToken')
const { messageModel } = require('./../model/message')
const { auth } = require('./../middleware/auth')
const { sendMsgToUserWithFCMToken } = require('./../fcm/sendFCMMsg')
const { userModel } = require('./../model/user')



function getCurrentTime(date) {
    var hours = date.getHours();
    var minutes = date.getMinutes();
    var ampm = hours >= 12 ? 'pm' : 'am';
    hours = hours % 12;
    hours = hours ? hours : 12; // the hour '0' should be '12'
    minutes = minutes < 10 ? '0' + minutes : minutes;
    var strTime = hours + ':' + minutes + ' ' + ampm;
    return strTime;
}

function getCurrentDate(date_ob) {
    let date = ("0" + date_ob.getDate()).slice(-2);
    // current month
    let month = ("0" + (date_ob.getMonth() + 1)).slice(-2);
    // current year
    let year = date_ob.getFullYear();
    // current hours
    let hours = date_ob.getHours();
    // current minutes
    let minutes = date_ob.getMinutes();
    // current seconds
    let seconds = date_ob.getSeconds();

    const ans = date + "/" + month + "/" + year
    return ans
}

function saveMessageInDatabase() {}


route.post('/', async(req, res) => {
    const error = new Error()
    try {
        auth(req, res, async(req, res) => {

            const sendersEmail = req.user.email;
            const receipentsEmail = req.body.email;
            const message = req.body.message;

            //Now first find the fcm token of the receipent from fcmTokeneModel
            const user = await userModel.findOne({
                email: receipentsEmail
            })
            if (user) {
                //User exists in the database
                const dateObj = new Date();
                const messageResponse = {
                    message: message,
                    sender: {
                        email: sendersEmail,
                        firstName: req.user.firstName,
                        lastName: req.user.lastName
                    },
                    recipient: {
                        email: receipentsEmail,
                        firstName: user.firstName,
                        lastName: user.lastName
                    },
                    timeOfMessage: getCurrentTime(dateObj),
                    dateOfMessage: getCurrentDate(dateObj)
                };
                //Now it may happen that the user is not logged in, but has an account. Therefore first search if fcmToken is not null
                if (user.fcmToken === 'null') {
                    //User has previously made an account, but is not currently logged in. Therefore just
                    //save this message in message table.
                } else {
                    //User exists and is logged in to the app too,
                    await sendMsgToUserWithFCMToken(user.fcmToken, messageResponse) //First wait for this to happen, if some error happens in sending the message.
                        //Then it should go to catch part.
                }
                saveMessageInDatabase()
                res.status(200).send(messageResponse)
            } else {
                error.message = "User doesn't exists";
                res.status(401).send(error)
            }
        })
    } catch (err) {
        console.log('erroororo', err)
        error.message = err.message
        res.status(401).send(error)
    }
})


exports = module.exports = {
    route
}