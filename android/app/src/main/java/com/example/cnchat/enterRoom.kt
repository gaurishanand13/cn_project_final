package com.example.cnchat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URI
import javax.net.ssl.SSLContext


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
                GlobalScope.launch {
                    connect()
                }
            }
        }
    }


    suspend fun connect(){
        try {
            val sc = SSLContext.getInstance("SSL")
            sc.init(null, null, null)
            val opts = IO.Options()
            opts.secure = true
            opts.forceNew = true
            opts.reconnection = true
            //https://192.168.0.18:5000
            val  socket = IO.socket(URI("https://192.168.0.18:5000")).also{

                Log.i("id= == ",it.connect().id())
            }

        } catch (e: Exception) {
            Log.i("socket is null", e.message.toString())
            e.printStackTrace()
        }
    }
}