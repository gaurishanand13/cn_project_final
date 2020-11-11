package com.example.cnchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.intentservice.chatui.ChatView
import co.intentservice.chatui.models.ChatMessage
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
                val userNameAdded = args.get(0) as JSONObject
                chatList.add(myMessage(userNameAdded.getString("userName"),socketHelper.roomName,"ADDED TO GROUP"))
                Log.i("updated chat list" , chatList.toString())
                runOnUiThread {
                    adapter.notifyDataSetChanged()
                }
            }
        })

        socketHelper.socket.on("userLeftChatRoom",object : Emitter.Listener{
            override fun call(vararg args: Any?) {
                val userNameLeft = args.get(0) as JSONObject
                chatList.add(myMessage(userNameLeft.getString("userName"),socketHelper.roomName,"LEFT GROUP"))
                runOnUiThread {
                    adapter.notifyDataSetChanged()
                }
            }
        })

        socketHelper.socket.on("updateChat",object : Emitter.Listener{
            override fun call(vararg args: Any?) {

                val json = args.get(0) as JSONObject

                val username = json.getString("userName")
                val messageContent = json.getString("messageContent")
                val roomName = json.getString("roomName")

                val newMessage = myMessage(username,roomName,messageContent)
                Log.i("newMessage",newMessage.toString())
                chatList.add(newMessage)

                runOnUiThread {
                    adapter.notifyDataSetChanged()
                }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val sendBtn = findViewById<ImageButton>(R.id.sendBtn)
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
        val jsonObject = JSONObject()
        jsonObject.put("userName",socketHelper.userName)
        jsonObject.put("roomName",socketHelper.roomName)
        socketHelper.socket.emit("subscribe",jsonObject)
    }

    fun unSubscribeUser(){
        val jsonObject = JSONObject()
        jsonObject.put("userName",socketHelper.userName)
        jsonObject.put("roomName",socketHelper.roomName)
        socketHelper.socket.emit("unsubscribe",jsonObject)
        socketHelper.socket.disconnect()
    }


    fun sendNewMessgae(msgContent : String, roomName : String){
        val jsonObject = JSONObject()
        jsonObject.put("userName",socketHelper.userName)
        jsonObject.put("messageContent",msgContent)
        jsonObject.put("roomName",socketHelper.roomName)
        socketHelper.socket.emit("newMessage",jsonObject)
    }

    override fun onDestroy() {
        super.onDestroy()
        unSubscribeUser()
    }

}