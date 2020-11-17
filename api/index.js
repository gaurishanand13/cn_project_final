const route = require('express').Router()



route.use('/login', require('./login').route)
route.use('/register', require('./register').route)
route.use('/getMessages', require('./getMessages').route)
route.use('/sendMessage', require('./sendMessage').route)
route.use('/updateFCMToken', require('./updateFCMToken').route)



exports = module.exports = {
    route
}