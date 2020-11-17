const { admin } = require('./firebase_config')

/**
 * 
 * @param {fcmToken of the user who is going to receive the message} fcmToken 
 * @param {message the fcm token user is going to receive } message 
 * @param {email of the user who is sending this message} sendersUserName 
 * @param {email of the user who is receiving this message i.e fcmToken user} receiversUserName 
 */
async function sendMsgToUserWithFCMToken(fcmToken, message, sendersUserName, receiversUserName) {
    try {
        const notification_options = {
            priority: "high", //High priority means that this message/notif will be delievered immediately.
            timeToLive: 60 * 60 * 24
        };
        //Note data in notification is always sent like this - 
        const notif_message = {
            notification: {
                title: `${sendersUserName} sent you a message`,
                body: message
            },
            data: {
                sendersEmail: sendersUserName,
                body: message,
                title: `${sendersUserName} sent you a message`,
                receipent: receiversUserName
            }
        }
        await admin.messaging().sendToDevice(fcmToken, notif_message, notification_options)
        console.log('notification successfully sent')
    } catch (error) {
        console.log('error in sending notification', error.message)
    }
}

exports = module.exports = {
    sendMsgToUserWithFCMToken
}