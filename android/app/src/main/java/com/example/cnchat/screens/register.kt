package com.example.cnchat.screens

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
        retrofitClient.retrofitService.registerUser(
            firstNameEditText.text.toString(),
            lastNameEditText.text.toString(),
            emailEditText.text.toString(),
            passwordEditText.text.toString()
        ).enqueue(object :
            Callback<loginRegisterResponse> {
            override fun onFailure(call: Call<loginRegisterResponse>, t: Throwable) {
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
                        sharedPref.putString(constants.token_name,res.token)
                        sharedPref.putString(constants.first_name,res.user?.firstName)
                        sharedPref.putString(constants.last_name,res.user?.lastName)
                        sharedPref.putString(constants.email,res.user?.email)
                        sharedPref.apply()
                        sharedPref.commit()


                        //Also make sure that the user FCM token is registered on the server
                        FirebaseMessaging.getInstance().token.addOnCompleteListener(
                            OnCompleteListener { task ->
                                if (!task.isSuccessful) {
                                    Log.i("err Fetching FCM token", task.exception.toString())
                                    return@OnCompleteListener
                                }

                                // Get new FCM registration token
                                val token = task.result
                                retrofitClient.retrofitService.updateFCMToken(constants.bearer + constants.token,token!!).enqueue(object : Callback<fcmTokenResponse>{
                                    override fun onFailure(call: Call<fcmTokenResponse>, t: Throwable) {
                                        Log.i("error in fcm login",t.message.toString())
                                        Toast.makeText(applicationContext,t.message,Toast.LENGTH_SHORT).show()
                                    }
                                    override fun onResponse(call: Call<fcmTokenResponse>, response: Response<fcmTokenResponse>) {
                                        if(response.isSuccessful){
                                            Toast.makeText(applicationContext,"Successfuly registered",Toast.LENGTH_SHORT).show()
                                            startActivity(Intent(this@register, chatList::class.java))
                                            finish()
                                        }
                                        else{

                                        }
                                    }
                                })
                            })
                    }
                    else{
                        Toast.makeText(this@register,"Error!",Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    val jsonObject = JSONObject(response.errorBody()?.string())
                    Toast.makeText(this@register,jsonObject.getString("message"),Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}