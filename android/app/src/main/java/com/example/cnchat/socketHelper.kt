package com.example.cnchat

import com.example.cnchat.retrofit.model.message
import com.github.nkzawa.socketio.client.Socket


object socketHelper{
    lateinit var socket : Socket
    lateinit var userName : String
    lateinit var messages : ArrayList<message>
}