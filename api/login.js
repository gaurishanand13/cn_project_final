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
                const data = {
                    error: "Invalid password"
                }
                res.status(401).send(data)
            }
        } else {
            //Email doesn't exist
            const data = {
                error: "Email not registered"
            }
            res.status(401).send(data)
        }
    } catch (error) {
        const data = {
            error: "Error! try again"
        }
        res.status(401).send(data)
    }
})


exports = module.exports = {
    route
}