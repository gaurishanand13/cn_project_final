package com.example.cnchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cnchat.adapters.chatRoomAdapter
import com.example.cnchat.adapters.roomChatInterface
import com.example.cnchat.model.myMessage
import com.example.cnchat.model.registerSocket
import com.github.nkzawa.emitter.Emitter
import com.google.gson.Gson
import org.json.JSONObject
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    val chatList : ArrayList<myMessage> = ArrayList()
    val gson = Gson()
    lateinit var adapter : chatRoomAdapter

    val chatRoomInterface = object : roomChatInterface{
        override fun onClick(position: Int) {

        }
    }

    fun registerOnMethodOfSocket(){
        socketHelper.socket.on("newUserToChatRoom",object : Emitter.Listener{
            override fun call(vararg args: Any?) {
                val userNameAdded = args.get(0) as String
                chatList.add(myMessage(userNameAdded,socketHelper.roomName,"ADDED TO GROUP"))
                adapter.notifyDataSetChanged()
            }
        })

        socketHelper.socket.on("userLeftChatRoom",object : Emitter.Listener{
            override fun call(vararg args: Any?) {
                val userNameLeft = args.get(0) as String
                chatList.add(myMessage(userNameLeft,socketHelper.roomName,"LEFT GROUP"))
                adapter.notifyDataSetChanged()
            }
        })

        socketHelper.socket.on("updateChat",object : Emitter.Listener{
            override fun call(vararg args: Any?) {
                val data = args.get(0) as JSONObject

                val username = data.getString("userName")
                val messageContent = data.getString("messageContent")
                val roomName = data.getString("messageContent")

                val newMessage = myMessage(username,roomName,messageContent)
                chatList.add(newMessage)

                adapter.notifyDataSetChanged()
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sendBtn = findViewById<Button>(R.id.sendBtn)
        val messageEditText = findViewById<EditText>(R.id.messageEditText)

        /**
         * Setting up the chat recyclerView
         */
        var messagelistRecylcerView : RecyclerView = findViewById(R.id.messagelistRecylcerView)
        messagelistRecylcerView.layoutManager = LinearLayoutManager(this)
        adapter = chatRoomAdapter(this,chatRoomInterface,chatList)
        messagelistRecylcerView.adapter = adapter

        sendBtn.setOnClickListener {
            if(!messageEditText.text.toString().isEmpty()){
                sendNewMessgae(messageEditText.text.toString(),socketHelper.roomName)
            }
        }

        //First when this activity opens, subscribe this user on the socket.
        subsribeUser()

        //Now register other on Method to handle data emitted from the server
        registerOnMethodOfSocket()
    }

    fun subsribeUser(){
        val jsonData = gson.toJson(registerSocket(socketHelper.userName,socketHelper.roomName))
        socketHelper.socket.emit("subscribe",jsonData)
        socketHelper.socket.disconnect()
    }

    fun unSubscribeUser(){
        val jsonData = gson.toJson(registerSocket(socketHelper.userName,socketHelper.roomName))
        socketHelper.socket.emit("unsubscribe",jsonData)
        socketHelper.socket.disconnect()
    }

    fun sendNewMessgae(msgContent : String, roomName : String){
        val jsonData = gson.toJson(myMessage(msgContent,roomName,socketHelper.userName))
        socketHelper.socket.emit("newMessage",jsonData)
        socketHelper.socket.disconnect()
    }

    override fun onDestroy() {
        super.onDestroy()
        unSubscribeUser()
    }

}