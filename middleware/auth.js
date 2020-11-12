const jwt = require('jsonwebtoken')
const { SECRET_KEY } = require('./../config')


function auth(req, res, callbackFunction) {
    try {
        const authHeader = req.headers.authorization
        if (authHeader) {
            const token = authHeader.split('Bearer ')[1];
            if (token) {
                const user = jwt.verify(token, SECRET_KEY);
                req.user = user //inserting the user details found through this token 
                callbackFunction(req, res) //Calling the callback function if user verified
            } else {
                const data = {
                    error: "authorization token must be a Bearer [token]"
                }
                res.status(402).send(data)
            }
        } else {
            const data = {
                error: "Authorization header not present"
            }
            res.status(402).send(data)
        }
    } catch (error) {
        const data = {
            error: "Error! Invalid / Expired Token"
        }
        res.status(402).send(data)
    }
}


exports = module.exports = {
    auth
}