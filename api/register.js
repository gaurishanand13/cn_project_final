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
    const firstName = req.body.firstName
    const lastName = req.body.lastName
    const email = req.body.email
    const password = req.body.password

    const error = new Error()

    console.log(firstName, lastName, email, password)

    try {
        //First check if the email entered is a unique one 
        const user = await userModel.findOne({
            email: email
        })

        if (user) {
            //If user is found ,  it means email is already used by someone else
            error.message = "Email already in use"
            res.status(400).send(error)

        } else {
            //Now since we have not found that user doesn't exist before ,  we can create a new user for this
            const newUser = new userModel({
                firstName: firstName,
                lastName: lastName,
                email: email,
                password: password
            })
            await newUser.save()
            const token = getSignedToken(newUser)
            const data = {
                token: token,
                user: {
                    firstName: firstName,
                    lastName: lastName,
                    email: email
                }
            }
            console.log(data)
            res.status(200).send(data)
        }
    } catch (error) {
        console.log('Error in register', error)
        error.message = "Error! try again"
        res.status(400).send(error)
    }
})


exports = module.exports = {
    route
}