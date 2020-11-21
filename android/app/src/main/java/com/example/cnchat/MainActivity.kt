package com.example.cnchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.*
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cnchat.adapters.chatRoomAdapter
import com.example.cnchat.retrofit.model.sendMessageResponse
import com.example.cnchat.retrofit.retrofitClient
import com.example.cnchat.room.models.friendsTable
import com.example.cnchat.room.models.messageTable
import com.example.cnchat.viewModel.particularChatViewModel
import com.example.cnchat.viewModel.particularChatViewModelFactory
//import com.example.cnchat.adapters.chatRoomAdapter
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }
    private val chatViewModel: particularChatViewModel by viewModels {
        particularChatViewModelFactory((application as myApplicationClass).repository)
    }


    fun sendMessage(receipentsEmail: String,messge : String,receipentsFirstName : String,receipentsLastName : String){
        retrofitClient.retrofitService.sendMessage(constants.bearer+constants.token,receipentsEmail,messge).enqueue(object : Callback<sendMessageResponse>{
            override fun onFailure(call: Call<sendMessageResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity,t.message,Toast.LENGTH_SHORT).show()
                Log.i("err in sending message",t.message.toString())
            }
            override fun onResponse(call: Call<sendMessageResponse>, response: Response<sendMessageResponse>) {
                if(response.code()==200){
                    //Now it means data is successfully sent to the other user too. Now let's save this too in our local database
                    addMessageToDB(response.body()!!)
                }
                else{
                    val jsonObject = JSONObject(response.errorBody()?.string())
                    Toast.makeText(this@MainActivity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    fun addMessageToDB(myMessage : sendMessageResponse){

        chatViewModel.insertMessage(messageTable(
                message = myMessage.message,
                sendersEmail = myMessage.sender.email,
                sendersFirstName = myMessage.sender.firstName,
                sendersLastName = myMessage.sender.lastName,
                recipientsEmail = myMessage.recipient.email,
                recipientsFirstName = myMessage.recipient.firstName,
                recipientsLastName = myMessage.recipient.lastName,
                dateofmessaging = myMessage.dateOfMessage,
                timeofmessaging = myMessage.timeOfMessage
        ))

        //Also update the last message in the table
        chatViewModel.insertUser(
                friendsTable(
                        lastMessageExchanged = myMessage.message,
                        friendsEmail = myMessage.recipient.email,
                        dateOfMessage = myMessage.dateOfMessage,
                        timeOfMessage = myMessage.timeOfMessage,
                        friendsFirstName = myMessage.recipient.firstName,
                        friendslastName = myMessage.recipient.lastName
                )
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val receipentsEmail = intent.getStringExtra("reqEmail")!!
        val receipentsFirstName = intent.getStringExtra("firstName")!!
        val receipentsLastName = intent.getStringExtra("lastName")!!

        //Setting up the back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back)
        //Setting the title of the activity
        supportActionBar?.title = "${receipentsFirstName} ${receipentsLastName}"


        //Setting up the adapter
        val list = ArrayList<messageTable>()
        val adapter = chatRoomAdapter(this,list)
        messagelistRecylcerView.layoutManager = LinearLayoutManager(this)
        messagelistRecylcerView.adapter = adapter
        chatViewModel.getCurrentMessagesLiveData(receipentsEmail)
            .observe(this, Observer {
                list.clear()
                list.addAll(it)
                adapter.notifyDataSetChanged()
                messagelistRecylcerView.scrollToPosition(it.size-1)
            })


        //Setting up the send message button
        sendBtn.setOnClickListener {
            if(!messageEditText.text.toString().isEmpty()){
                //Send the message to the user
                sendMessage(receipentsEmail!!,messageEditText.text.toString(),receipentsFirstName,receipentsLastName)

                //Setting the text as empty - after the message is sent.
                messageEditText.setText("",TextView.BufferType.EDITABLE)
            }
        }
    }



}