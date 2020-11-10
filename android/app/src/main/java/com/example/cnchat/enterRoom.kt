package com.example.cnchat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket


class enterRoom : AppCompatActivity() {


    private var socket: Socket? = null;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_room)

        val username = findViewById<EditText>(R.id.username)
        val roomname = findViewById<EditText>(R.id.roomName)

        val enterRoomBtn = findViewById<Button>(R.id.enterRoomBtn)

        enterRoomBtn.setOnClickListener {
            if(username.text.toString().isEmpty() || roomname.text.toString().isEmpty()){
                Toast.makeText(this,"Enter name and username both", Toast.LENGTH_SHORT).show()
            }
            else{
                socketHelper.userName = username.text.toString()
                socketHelper.roomName = roomname.text.toString()
                connect()
            }
        }
    }


    fun connect(){
        try {
            val opts = IO.Options()
            opts.timeout = 3000
            opts.reconnection = true
            opts.reconnectionAttempts = 10
            opts.reconnectionDelay = 1000
            opts.forceNew = true
            socketHelper.socket = IO.socket("http://192.168.0.18:1234/")
            //
            if (socketHelper.socket == null) {
                Log.i("socket is null", "null")
            }
            else{
                Log.i("socket is null", "not null")
            }
        } catch (e: Exception) {
            Log.i("socket is null", e.message.toString())
            e.printStackTrace()
        }
        socketHelper.socket.connect()
        Log.i("hi",socketHelper.socket.id().toString())
        startActivity(Intent(this,MainActivity::class.java))
    }
}