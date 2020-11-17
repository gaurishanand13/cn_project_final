const jwt = require('jsonwebtoken')
const { SECRET_KEY } = require('./../config')


function auth(req, res, callbackFunction) {
    const error = new Error()
    try {
        const authHeader = req.headers.authorization
        if (authHeader) {
            const token = authHeader.split('Bearer ')[1];
            if (token) {
                const user = jwt.verify(token, SECRET_KEY);
                req.user = user //inserting the user details found through this token 
                callbackFunction(req, res) //Calling the callback function if user verified
            } else {
                error.message = "authorization token must be a Bearer [token]"
                res.status(402).send(error)
            }
        } else {
            error.message = "Authorization header not present"
            res.status(402).send(error)
        }
    } catch (error) {
        error.message = "Error! Invalid / Expired Token"
        res.status(402).send(error)
    }
}


exports = module.exports = {
    auth
}