package com.example.cnchat.retrofit.model

data class sendMessageResponse(
    val dateOfMessage: String,
    val message: String,
    val recipient: Recipient,
    val sender: Sender,
    val timeOfMessage: String
)