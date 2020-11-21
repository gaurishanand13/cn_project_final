const { admin } = require('./firebase_config')

async function sendMsgToUserWithFCMToken(fcmToken, messageResponse) {
    try {
        const notification_options = {
            priority: "high", //High priority means that this message/notif will be delievered immediately.
            timeToLive: 60 * 60 * 24
        };
        //Note data in notification is always sent like this - 
        const notif_message = {
            data: {
                message: messageResponse.message,
                sendersEmail: messageResponse.sender.email,
                sendersfirstName: messageResponse.sender.firstName,
                senderslastName: messageResponse.sender.lastName,
                recipientsEmail: messageResponse.recipient.email,
                recipientsfirstName: messageResponse.recipient.firstName,
                recipientslastName: messageResponse.recipient.lastName,
                timeOfMessage: messageResponse.timeOfMessage,
                dateOfMessage: messageResponse.dateOfMessage
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