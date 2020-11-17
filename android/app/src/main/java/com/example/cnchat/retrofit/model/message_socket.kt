package com.example.cnchat.retrofit.model

import java.sql.Date
import java.sql.Time

data class message(
    val sender : String,
    val recipient : String,
    val messageContent: String,
    val date : Date,
    val time : Time
)