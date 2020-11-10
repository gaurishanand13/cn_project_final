package com.example.cnchat

import com.github.nkzawa.socketio.client.Socket


object socketHelper{
    lateinit var socket : Socket
    lateinit var userName : String
    lateinit var roomName : String
}