package com.example.cnchat

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.intentservice.chatui.ChatView
import co.intentservice.chatui.models.ChatMessage
import com.example.cnchat.adapters.chatRoomAdapter
import com.example.cnchat.retrofit.model.fcmTokenResponse
import com.example.cnchat.retrofit.retrofitClient
import com.example.cnchat.room.models.friendsTable
import com.example.cnchat.room.models.messageTable
import com.example.cnchat.screens.chatList
import com.example.cnchat.viewModel.messageViewModel
import com.example.cnchat.viewModel.messageViewModelFactory
import com.example.cnchat.viewModel.particularChatViewModel
import com.example.cnchat.viewModel.particularChatViewModelFactory
//import com.example.cnchat.adapters.chatRoomAdapter
import com.github.nkzawa.emitter.Emitter
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {


    private val chatViewModel: particularChatViewModel by viewModels {
        particularChatViewModelFactory((application as myApplicationClass).repository)
    }

    fun sendMessage(receipentsEmail : String,messge : String){
        retrofitClient.retrofitService.sendMessage(constants.bearer+constants.token,receipentsEmail,messge).enqueue(object : Callback<fcmTokenResponse>{
            override fun onFailure(call: Call<fcmTokenResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity,t.message,Toast.LENGTH_SHORT).show()
                Log.i("err in sending message",t.message.toString())
            }

            override fun onResponse(call: Call<fcmTokenResponse>, response: Response<fcmTokenResponse>) {
                if(response.code()==200){
                    //Now it means data is successfully sent to the other user too. Now let's save this too in our local database
                    addMessageToDB(receipentsEmail,messge)
                }
                else{
                    val jsonObject = JSONObject(response.errorBody()?.string())
                    Toast.makeText(this@MainActivity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
                }
            }
        })
    }



    fun addMessageToDB(email : String,messge : String){

        val c: Date = Calendar.getInstance().getTime()
        //Setting the date
        var postFormater = SimpleDateFormat("MMMM dd, yyyy")
        val newDateStr: String = postFormater.format(c)

        //"hh:mm a"
        postFormater = SimpleDateFormat("hh:mm a")
        val time = postFormater.format(c)


        chatViewModel.insertMessage(messageTable(
            text = messge,
            sender = constants.usersEmail,
            recipient = email,
            dateofmessaging = newDateStr,
            timeofmessaging = time
        ))

        //Also update the last message in the table
        GlobalScope.launch {
            chatViewModel.isUserExists(
                email
            ).also {
                if(it.size==0){
                    //Then insert the user
                    chatViewModel.insertUser(
                        friendsTable(lastMessageExchanged = messge, friendsEmail= email, date = constants.fromDate(c))
                    )
                }
                else{
                    //Otherwise update the user in the room database
                    it[0].lastMessageExchanged = messge
                    it[0].date = constants.fromDate(c)
                    chatViewModel.updateUser(it[0])
                }
            }
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val receipents = intent.getStringExtra("reqEmail")


        sendBtn.setOnClickListener {
            if(!messageEditText.text.toString().isEmpty()){
                //Send the message to the user
                sendMessage(receipents!!,messageEditText.text.toString())

                //Setting the text as empty - after the message is sent.
                messageEditText.setText("",TextView.BufferType.EDITABLE)
            }
        }

        //Setting up the adapter
        val list = ArrayList<messageTable>()
        val adapter = chatRoomAdapter(this,list)
        messagelistRecylcerView.layoutManager = LinearLayoutManager(this)
        messagelistRecylcerView.adapter = adapter
        chatViewModel.getCurrentMessagesLiveData(intent.getStringExtra("reqEmail")!!)
            .observe(this, Observer {
                list.clear()
                list.addAll(it)
                adapter.notifyDataSetChanged()
                messagelistRecylcerView.scrollToPosition(it.size-1)
            })
    }

}