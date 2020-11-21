package com.example.cnchat.retrofit

import com.example.cnchat.retrofit.model.fcmTokenResponse
import com.example.cnchat.retrofit.model.loginRegisterResponse
import com.example.cnchat.retrofit.model.sendMessageResponse
import retrofit2.http.*

interface apiService{

    @POST("login")
    @FormUrlEncoded
    fun loginUser(
        @Field("email") email:String,
        @Field("password") password:String
    ) : retrofit2.Call<loginRegisterResponse>


    @POST("register")
    @FormUrlEncoded
    fun registerUser(
        @Field("firstName") firstName:String,
        @Field("lastName") lastName:String,
        @Field("email") email:String,
        @Field("password") password:String
    ) : retrofit2.Call<loginRegisterResponse>


    @POST("updateFCMToken")
    @FormUrlEncoded
    fun updateFCMToken(
        @Header("Authorization") authorization: String,
        @Field("fcmToken") fcmToken:String
    ) : retrofit2.Call<fcmTokenResponse>


    @POST("addUser")
    @FormUrlEncoded
    fun addUser(
            @Header("Authorization") authorization: String,
            @Field("email") email:String
    ) : retrofit2.Call<sendMessageResponse>


    @POST("sendMessage")
    @FormUrlEncoded
    fun sendMessage(
        @Header("Authorization") authorization: String,
        @Field("email") email:String,
        @Field("message") message:String
    ) : retrofit2.Call<sendMessageResponse>


}