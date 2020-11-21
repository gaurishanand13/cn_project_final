package com.example.cnchat.screens

import android.app.ProgressDialog
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
import com.example.cnchat.retrofit.model.sendMessageResponse
import com.example.cnchat.retrofit.retrofitClient
import com.example.cnchat.room.models.friendsTable
import com.example.cnchat.room.models.messageTable
import com.example.cnchat.viewModel.messageViewModel
import com.example.cnchat.viewModel.messageViewModelFactory
import com.github.nkzawa.socketio.client.IO
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.content_chat_list.*
import kotlinx.android.synthetic.main.create_room_alert_dialog.view.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Now what i will be doing is -
 * I will get the list of all users that this username has previously talked to.
 * After getting the list when user clicks on someone, then i will
 */

class chatList : AppCompatActivity() {

    override fun onBackPressed() {
        finishAffinity()
        super.onBackPressed()
    }
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


    val list = ArrayList<friendsTable>() //This is the chat list adapter which will be displayed in the dashboard adapter


    fun navigateToChatActivity(friendsEmail : String,firstName : String,lastName : String){
        val intent = Intent(this@chatList,MainActivity::class.java)
        intent.putExtra("reqEmail",friendsEmail)
        intent.putExtra("firstName",firstName)
        intent.putExtra("lastName",lastName)
        startActivity(intent)
    }
    fun setUpActivity(){
        //First set up the main adapter
        val myInterface = object  : chatListInterface{
            override fun onClick(position: Int) {
                //Setting up on Click for the app
                navigateToChatActivity(list.get(position).friendsEmail,list.get(position).friendsFirstName,list.get(position).friendslastName)
            }
        }
        val adapter = chatListAdapter(this,myInterface,list)
        chatListRecyclerView.layoutManager = LinearLayoutManager(this)
        chatListRecyclerView.adapter = adapter

        wordViewModel.allUsers.observe(this, androidx.lifecycle.Observer{
            list.clear()
            list.addAll(it.reversed())
            adapter.notifyDataSetChanged()
        })
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)
        setSupportActionBar(findViewById(R.id.toolbar))

        setUpActivity()

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
                Toast.makeText(this,"Enter some user's email id",Toast.LENGTH_SHORT).show()
            }else{
                val progressDialog = ProgressDialog(this)
                progressDialog.setMessage("ADDING USER..")
                progressDialog.show()

                //Send the message to the user using the server
                val receipentsEmail = dialogView.enterRoomNameEditText.text.toString()

                //Now check if user exists or not first, then perform these actions
                retrofitClient.retrofitService.addUser(constants.bearer + constants.token, receipentsEmail).enqueue(object : Callback<sendMessageResponse>{

                    override fun onFailure(call: Call<sendMessageResponse>, t: Throwable) {
                        Toast.makeText(this@chatList,t.message,Toast.LENGTH_SHORT).show()
                        progressDialog.dismiss()
                        Log.i("err in checking user",t.message.toString())
                    }

                    override fun onResponse(call: Call<sendMessageResponse>, response: Response<sendMessageResponse>) {
                        progressDialog.dismiss()

                        if(response.code()==200){
                            //Now it means user exists
                            alertDialog.dismiss()
                            navigateToChatActivity(response.body()?.recipient!!.email,response.body()?.recipient!!.firstName,response.body()?.recipient!!.lastName)
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

}