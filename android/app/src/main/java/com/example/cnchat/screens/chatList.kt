package com.example.cnchat.screens

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cnchat.*
import com.example.cnchat.adapters.chatListAdapter
import com.example.cnchat.adapters.chatListInterface
import com.example.cnchat.retrofit.model.fcmTokenResponse
import com.example.cnchat.retrofit.retrofitClient
import com.example.cnchat.room.models.friendsTable
import com.example.cnchat.room.models.messageTable
import com.example.cnchat.viewModel.messageViewModel
import com.example.cnchat.viewModel.messageViewModelFactory
import com.github.nkzawa.socketio.client.IO
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.content_chat_list.*
import kotlinx.android.synthetic.main.create_room_alert_dialog.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Now what i will be doing is -
 * I will get the list of all users that this username has previously talked to.
 * After getting the list when user clicks on someone, then i will
 */

class chatList : AppCompatActivity() {


    private val wordViewModel: messageViewModel by viewModels {
        messageViewModelFactory((application as myApplicationClass).repository)
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.logout){
            val sharedPref = this@chatList.getSharedPreferences(
                    constants.sharedPrefName,
                    Context.MODE_PRIVATE).edit()

            sharedPref.putString(constants.token_name,"")
            sharedPref.apply()
            sharedPref.commit()
            startActivity(Intent(this,login::class.java))
        }
        finish()
        return super.onOptionsItemSelected(item)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chat_list_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    fun setUpActivity(){
        //First set up the main adapter
        val list = ArrayList<friendsTable>()
        val myInterface = object  : chatListInterface{
            override fun onClick(position: Int) {
                //Setting up on Click for the app
                val intent = Intent(this@chatList,MainActivity::class.java)
                intent.putExtra("reqEmail",list.get(position).friendsEmail)
                startActivity(intent)
            }
        }
        val adapter = chatListAdapter(this,myInterface,list)
        chatListRecyclerView.layoutManager = LinearLayoutManager(this)
        chatListRecyclerView.adapter = adapter

        wordViewModel.allUsers.observe(this, androidx.lifecycle.Observer{
            list.clear()
            list.addAll(it)
            adapter.notifyDataSetChanged()
        })
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_chat_list)
        setSupportActionBar(findViewById(R.id.toolbar))

        /**
         * First set the users email from the shared preferences as it will be required here.
         */
        val sharedPref = this@chatList.getSharedPreferences(constants.sharedPrefName, Context.MODE_PRIVATE)
        constants.usersEmail = sharedPref.getString(constants.email,"")!!

        setUpActivity()
//        //First set up the socket
//        GlobalScope.launch {
//            setUpSocket()
//        }

        //Setting up the floating action button
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            generateAlertDialog()
        }
    }

    suspend fun setUpSocket(){
        try {
            val sharedPref = this.getSharedPreferences(constants.sharedPrefName, Context.MODE_PRIVATE)
            socketHelper.userName = sharedPref.getString(constants.email,"")!!

            socketHelper.socket = IO.socket("http://192.168.0.18:3000/")
            socketHelper.socket.connect()

            //First register the socket on the socketMap on server
            val jsonObject = JSONObject()
            jsonObject.put("username",socketHelper.userName)
            socketHelper.socket.emit("userOnline",jsonObject)

            //Now get all the previousChats of the user and store it in message, after storing previous chats get

        } catch (e: Exception) {
            Log.i("socket is null", e.message.toString())
            Toast.makeText(this,"unable to connect to socket",Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }


    fun generateAlertDialog(){
        val dialog = AlertDialog.Builder(this)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.create_room_alert_dialog,null)
        dialog.setView(dialogView)
        dialog.setCancelable(true)

        val alertDialog = dialog.create()
        dialogView.enterRoomButton.setOnClickListener {
            if(dialogView.enterRoomNameEditText.text.toString().isEmpty()){
                Toast.makeText(this,"Enter room name",Toast.LENGTH_SHORT).show()
            }else{
                //Send the message to the user using the server
                retrofitClient.retrofitService.sendMessage(
                    constants.bearer + constants.token,
                    dialogView.enterRoomNameEditText.text.toString(), "${constants.usersEmail} ADDED YOU").enqueue(object : Callback<fcmTokenResponse>{
                    override fun onFailure(call: Call<fcmTokenResponse>, t: Throwable) {
                        Toast.makeText(this@chatList,t.message,Toast.LENGTH_SHORT).show()
                        Log.i("err in sending message",t.message.toString())
                    }

                    override fun onResponse(call: Call<fcmTokenResponse>, response: Response<fcmTokenResponse>) {
                        if(response.code()==200){
                            //Now it means data is successfully sent to the other user too. Now let's save this too in our local database
                            alertDialog.dismiss()
                            addMessageToDB(dialogView.enterRoomNameEditText.text.toString(),"${constants.usersEmail} ADDED YOU")
                        }
                        else{
                            val jsonObject = JSONObject(response.errorBody()?.string())
                            Toast.makeText(this@chatList, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }
        }
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
    }

    fun addMessageToDB(email : String,messge : String){

        val c: Date = Calendar.getInstance().getTime()
        //Setting the date
        var postFormater = SimpleDateFormat("MMMM dd, yyyy")
        val newDateStr: String = postFormater.format(c)

        //"hh:mm a"
        postFormater = SimpleDateFormat("hh:mm a")
        val time = postFormater.format(c)


        wordViewModel.insertMessage(
            messageTable(
            text = messge,
            sender = constants.usersEmail,
            recipient = email,
            dateofmessaging = newDateStr,
            timeofmessaging = time
        ))

        //Also update the last message in the table
        GlobalScope.launch {
            wordViewModel.isUserExists(
                email
            ).also {
                if(it.size==0){
                    //Then insert the user
                    wordViewModel.insertUser(
                        friendsTable(lastMessageExchanged = messge, friendsEmail= email, date = constants.fromDate(c))
                    )
                }
                else{
                    //Otherwise update the user in the room database
                    it[0].lastMessageExchanged = messge
                    it[0].date = constants.fromDate(c)
                    wordViewModel.updateUser(it[0])
                }
            }
        }
    }
}