const LocalStrategy = require("passport-local").Strategy
const bcrypt = require("bcrypt")

async function initialize(passport, getUserByUsername){

    const authenticateUser = async (username, password, done) => {
        const user = getUserByUsername(username)
        if(user == null){
            return done(null, false, {message: "no user with username"})
        }

        try{
            if(await bcrypt.compare(password, user.password)){
                return done(null, user)
            }else{
                return done(null, false, {message: "invalid password"})
            }
        }catch(e){
            return done(e)
        }
    }

    passport.use(new LocalStrategy({usernameField: "username"}, 
    authenticateUser))
    passport.serializeUser((user, done) => done(null, user.username))
    passport.deserializeUser((username, done) => {
        done(null, getUserByUsername(username))
    })
}

module.exports = initialize