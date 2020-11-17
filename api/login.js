const route = require('express').Router();

const jwt = require('jsonwebtoken')

const { userModel } = require('./../model/user')
const { SECRET_KEY } = require('./../config');


function getSignedToken(user) {
    //This token will contain the information encoded in itself
    return jwt.sign({
        id: user._id,
        email: user.email,
        firstName: user.firstName,
        lastName: user.lastName
    }, SECRET_KEY, { expiresIn: '10000000h' });
}


route.post('/', async(req, res) => {
    const email = req.body.email
    const password = req.body.password
    const error = new Error()

    console.log(email, password)
    try {
        //First check if the email entered is a unique one 
        const user = await userModel.findOne({
            email: email
        })

        if (user) {
            //If user is found ,  then check if password is valid or not
            const isValidPass = await user.isPasswordValid(password)
            if (isValidPass) {
                const data = {
                    token: getSignedToken(user),
                    user: {
                        firstName: user.firstName,
                        lastName: user.lastName,
                        email: email
                    }
                }
                res.status(201).send(data)
            } else {
                error.message = "Invalid Password"
                res.status(401).send(error)
            }
        } else {
            //Email doesn't exist
            error.message = "Email not Registered"
            res.status(401).send(error)
        }
    } catch (error) {
        error.message = "Error! Try again"
        res.status(401).send(error)
    }
})


exports = module.exports = {
    route
}