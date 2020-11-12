const mongoose = require('mongoose')
const Schema = mongoose.Schema
const bcrypt = require('bcrypt')


//This function checks if the entered email is in correct format or not (through regex)
function emailValidator(value) {
    return /^.+@.+\..+$/.test(value);
}

const userSchema = new Schema({
    firstName: {
        type: String,
        required: true
    },
    lastName: {
        type: String,
        required: true
    },
    email: {
        type: String,
        required: true,
        unique: true,
        lowercase: true,
        validate: [emailValidator, 'Incorrect Email Format'] //If we have sent an user with email not in correct format, then the user will not 
            //be saved to the database, instead this error will be thrown
    },
    password: {
        type: String,
        required: true
    }
})

/**
 * Now user bcrypt too i.e before saving the data in the database, ask the scheme to bcrypt the password to some unreadable format so that even
 * if we give access to someUser , he is not able to read the password of others
 */
userSchema.pre('save', async function(next) {
    //This function will be executed before saving every user
    try {
        const salt = await bcrypt.genSalt(10);
        console.log('password in hasher = ', this.password, this)
        const passwordHash = await bcrypt.hash(this.password, salt);
        this.password = passwordHash;
        next();
    } catch (error) {
        next(error);
    }
})


//Now also let's attach some functions to this table which can be used later whenever we want to
//This method is used whenever user wants to login to this app and we want to check whether the entered password is correct or not
userSchema.methods.isPasswordValid = async function(value) {
    try {
        //bcrypt will automatically compare the hashed password with the actual password by hashing it again
        return await bcrypt.compare(value, this.password);
    } catch (error) {
        throw new Error(error);
    }
}

const userModel = mongoose.model('user', userSchema)

exports = module.exports = {
    userModel
}