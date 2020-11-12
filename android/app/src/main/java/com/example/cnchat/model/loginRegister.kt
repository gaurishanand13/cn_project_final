package com.example.cnchat.model

data class loginRegisterResponse(
    val error : String?,
    val token : String?,
    var user : User?
)