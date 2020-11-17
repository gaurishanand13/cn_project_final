const { fcmTokeneModel } = require('./model/fcmToken')

async function xx() {
    try {
        const users = await fcmTokeneModel.find({})
        console.log('--------------------')
        console.log(users)
    } catch (error) {
        console.log(error.message)
    }
}
xx()