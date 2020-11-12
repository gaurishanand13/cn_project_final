package com.example.cnchat.screens

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.cnchat.R
import android.content.Context;
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.cnchat.constants
import com.example.cnchat.enterRoom
import com.example.cnchat.model.loginRegisterResponse
import com.example.cnchat.retrofit.retrofitClient
import kotlinx.android.synthetic.main.layout_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class login : AppCompatActivity() {

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        cirLoginButton.setOnClickListener {
            if(editTextEmail.text.toString().isEmpty() || editTextPassword.text.toString().isEmpty()){
                Toast.makeText(this,"Enter email and password both",Toast.LENGTH_SHORT).show()
            }
            else{
                performLogin()
            }
        }
        signUpTextView.setOnClickListener {
            startActivity(Intent(this,register::class.java))
        }
    }


    fun performLogin(){
        retrofitClient.retrofitService.loginUser(editTextEmail.text.toString(),editTextPassword.text.toString()).enqueue(object : Callback<loginRegisterResponse>{
            override fun onFailure(call: Call<loginRegisterResponse>, t: Throwable) {
                Log.i("error",t.message.toString())
                t.printStackTrace()
                Toast.makeText(this@login,t.message,Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<loginRegisterResponse>, response: Response<loginRegisterResponse>) {
                val res = response.body()
                if(res?.error!=null){
                    Toast.makeText(this@login,res.error,Toast.LENGTH_SHORT).show()
                }
                else if(res?.user!=null){
                    val sharedPref = this@login.getSharedPreferences(constants.sharedPrefName,Context.MODE_PRIVATE).edit()
                    Log.i("token = ",res.token.toString())
                    Log.i("user = ",res.user.toString())
                    constants.token = res.token!!
                    sharedPref.putString(constants.token,res.token)
                    sharedPref.putString(constants.first_name,res.user?.firstName)
                    sharedPref.putString(constants.last_name,res.user?.lastName)
                    sharedPref.putString(constants.email,res.user?.email)
                    sharedPref.apply()
                    sharedPref.commit()

                    startActivity(Intent(this@login,enterRoom::class.java))
                    finish()
                }
                else{
                    Toast.makeText(this@login,"Error!",Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}