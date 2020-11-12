module.exports = {
    DATABASE_NAME: 'chatAppDB',
    MONGODB_URL: `mongodb://localhost:27017/chatAppDB`, //Here last end point is usually the dabase name
    SECRET_KEY: 'personalnotesmgrkey' //This secret key will be used by jwt to generate the token, this string will be used for reference
};