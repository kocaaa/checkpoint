if (process.env.NODE_ENV !== 'production') {
    require('dotenv').config()
}

const express = require('express')
const app = express()
const bcrypt = require('bcrypt')
const passport = require('passport')
const flash = require('express-flash')
const session = require('express-session')
const methodOverride = require("method-override")
const initializePassport = require("./passport-config")

users = [{
    username: 'admin',
    password: '$2b$10$R453Towq0V7EViltH59XcOnv.ra/lP1ihB.m8Udiq32mZOcvH5pjq'
    }]

// $2b$10$TtsjNmZKlohLU8qRjoL83u
// $2b$10$TtsjNmZKlohLU8qRjoL83uxupl2kLDN6PpIL1eKjsU43fahF4GQuO

initializePassport(
    passport, 
    username => users.find(user => user.username == username)
)

app.use(express.json())
app.set('view-engine', 'ejs')
app.use(express.urlencoded({ extended: false }))
app.use(flash())
app.use(session({
    secret: process.env.SESSION_SECRET,
    resave: false,
    saveUninitialized: false
    }))
app.use(passport.initialize())
app.use(passport.session())
app.use(methodOverride("_method"))

app.get("/", checkAuthenticated, (req,res) =>{
    res.render("index.ejs", {  })
})

app.get("/login", checkNotAuthenticated, (req,res) =>{
    res.render("login.ejs",)
})

app.post("/login", checkNotAuthenticated, passport.authenticate('local', {
    successRedirect: "/",
    failureRedirect: '/login',
    failureFlash: true
}))

app.post("/user", async (req, res) => {
    try{
        const salt = await bcrypt.genSalt()
        const hashedPassword = await bcrypt.hash(req.body.password, salt)
        console.log("--")
        console.log(hashedPassword)

        
        console.log(salt)
        console.log(hashedPassword)
        const user = {username: req.body.username, password: hashedPassword}
        users.push(user)
        console.log(users)
        res.status(201).send()
    }
    catch{
        console.log("error")
        res.status(500).send()
    }
})

function checkAuthenticated(req, res, next){
    if (req.isAuthenticated()){
        return next()
    }
    res.redirect("/login")
}

app.delete("/logout", (req, res) => {
    req.logout(function(err) {
        if (err) { return next(err); }
        res.redirect('/login');
      });
})

app.get("/download", checkAuthenticated, (req, res) =>{
    res.download("./apk/checkpoint.apk")
})

function checkNotAuthenticated(req, res, next){
    if (req.isAuthenticated()){
        return res.redirect("/")
    }
    next()
}

app.listen(10069)