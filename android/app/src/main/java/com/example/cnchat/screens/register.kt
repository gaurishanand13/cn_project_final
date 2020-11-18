package com.example.cnchat.screens

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.example.cnchat.R
import com.example.cnchat.constants
import com.example.cnchat.retrofit.model.fcmTokenResponse
import com.example.cnchat.retrofit.model.loginRegisterResponse
import com.example.cnchat.retrofit.retrofitClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.layout_register.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class register : AppCompatActivity() {


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //Setting up the back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back)

        registerButton.setOnClickListener {
            if(firstNameEditText.text.toString().isEmpty() ||
                lastNameEditText.text.toString().isEmpty() ||
                emailEditText.text.toString().isEmpty() ||
                passwordEditText.text.toString().isEmpty() ){

                Toast.makeText(this,"Enter all fields",Toast.LENGTH_SHORT).show()
            }
            else{
                performRegister()
            }
        }

        signInTextView.setOnClickListener {
            finish()
        }
    }


    fun performRegister(){

        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Registering..")
        progressDialog.show()


        retrofitClient.retrofitService.registerUser(firstNameEditText.text.toString(), lastNameEditText.text.toString(), emailEditText.text.toString(), passwordEditText.text.toString()).enqueue(object : Callback<loginRegisterResponse> {

            override fun onFailure(call: Call<loginRegisterResponse>, t: Throwable) {
                progressDialog.dismiss()
                Toast.makeText(this@register,t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<loginRegisterResponse>, response: Response<loginRegisterResponse>) {

                if(response.code()==200){
                    val res = response.body()
                    if(res?.user!=null){
                        val res = response.body()
                        val sharedPref = this@register.getSharedPreferences(
                            constants.sharedPrefName,
                            Context.MODE_PRIVATE).edit()

                        constants.token = res?.token!!
                        constants.usersEmail = res.user?.email!!
                        sharedPref.putString(constants.token_name,res.token)
                        sharedPref.putString(constants.first_name,res.user?.firstName)
                        sharedPref.putString(constants.last_name,res.user?.lastName)
                        sharedPref.putString(constants.email,res.user?.email)
                        sharedPref.apply()
                        sharedPref.commit()


                        //Also make sure that the user FCM token of the user is registered on the server
                        try {
                            FirebaseMessaging.getInstance().token
                                    .addOnCompleteListener {
                                        if (!it.isSuccessful) {
                                            progressDialog.dismiss()
                                            Log.i("err Fetching FCM token", it.exception.toString())
                                        } else {
                                            // Get new FCM registration token
                                            val token = it.result
                                            Log.i("FCM token", token.toString())
                                            retrofitClient.retrofitService.updateFCMToken(constants.bearer + constants.token, token!!).enqueue(object : Callback<fcmTokenResponse> {
                                                override fun onFailure(call: Call<fcmTokenResponse>, t: Throwable) {
                                                    Log.i("error in fcm register", t.message.toString())
                                                    progressDialog.dismiss()
                                                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
                                                }

                                                override fun onResponse(call: Call<fcmTokenResponse>, response: Response<fcmTokenResponse>) {
                                                    progressDialog.dismiss()
                                                    if (response.isSuccessful) {
                                                        Toast.makeText(applicationContext, "Registeration Successful", Toast.LENGTH_SHORT).show()
                                                        startActivity(Intent(this@register, chatList::class.java))
                                                        finish()
                                                    } else {
                                                        Toast.makeText(applicationContext,"Error in saving fcm token",Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            })
                                        }
                                    }.addOnFailureListener {
                                        progressDialog.dismiss()
                                        Log.i("err Fetching FCM token", it.message.toString())
                                    }
                        } catch (e: Exception) {
                            progressDialog.dismiss()
                            Log.i("myException", e.message.toString())
                        }
                    }
                    else{
                        progressDialog.dismiss()
                        Toast.makeText(this@register,"Error!",Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    progressDialog.dismiss()
                    val jsonObject = JSONObject(response.errorBody()?.string())
                    Toast.makeText(this@register,jsonObject.getString("message"),Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}