package com.example.cnchat.retrofit

import android.telecom.Call
import com.example.cnchat.model.loginRegisterResponse
import retrofit2.http.*

interface apiService{

    @FormUrlEncoded
    @POST("/login")
    fun loginUser(
        @Field("email") email:String,
        @Field("password") password:String
    ) : retrofit2.Call<loginRegisterResponse>


    @FormUrlEncoded
    @POST("register")
    fun registerUser(
        @Field("firstName") firstName:String,
        @Field("lastName") lastName:String,
        @Field("email") email:String,
        @Field("password") password:String
    ) : retrofit2.Call<loginRegisterResponse>


    @GET("getChats")
    fun getAllThefavouriteTVShows(@Query("session_id") sessionID : String, @Query("page") page :Int) : Call


}