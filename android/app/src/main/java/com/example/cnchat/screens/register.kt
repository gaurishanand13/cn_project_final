package com.example.cnchat.screens

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import com.example.cnchat.R
import com.example.cnchat.constants
import com.example.cnchat.enterRoom
import com.example.cnchat.model.loginRegisterResponse
import com.example.cnchat.retrofit.retrofitClient
import kotlinx.android.synthetic.main.layout_login.*
import kotlinx.android.synthetic.main.layout_register.*
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



        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back)

        registerButton.setOnClickListener {
            if(firstNameEditText.text.toString().isEmpty() ||
                lastNameEditText.text.toString().isEmpty() ||
                emailEditText.text.toString().isEmpty() ||
                passwordEditText.text.toString().isEmpty() ){


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
                val res = response.body()
                if(res?.error!=null){
                    Toast.makeText(this@register,res.error, Toast.LENGTH_SHORT).show()
                }
                else if(res?.user!=null){
                    val sharedPref = this@register.getSharedPreferences(
                        constants.sharedPrefName,
                        Context.MODE_PRIVATE).edit()
                    constants.token = res.token!!
                    sharedPref.putString(constants.token,res.token)
                    sharedPref.putString(constants.first_name,res.user?.firstName)
                    sharedPref.putString(constants.last_name,res.user?.lastName)
                    sharedPref.putString(constants.email,res.user?.email)
                    sharedPref.apply()
                    sharedPref.commit()

                    startActivity(Intent(this@register, enterRoom::class.java))
                    finish()
                }
                else{
                    Toast.makeText(this@register,"Error!", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}