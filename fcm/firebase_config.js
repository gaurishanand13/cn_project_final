var admin = require("firebase-admin");

//Download this private key from firebase console -> project setting -> Service Accounts -> Download private key
var serviceAccount = require("./private_key.json");


admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: "https://tinderclone-2e750.firebaseio.com"
})

module.exports.admin = admin