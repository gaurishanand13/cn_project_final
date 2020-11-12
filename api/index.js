const route = require('express').Router()



route.use('/login', require('./login').route)
route.use('/register', require('./register').route)



exports = module.exports = {
    route
}