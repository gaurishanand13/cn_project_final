package com.example.cnchat

import androidx.room.TypeConverter
import java.util.*

object constants{
    val sharedPrefName = "mySharedPref"
    val baseURL = "http://192.168.0.18:3000/api/"
    val bearer = "Bearer "
    var token = ""
    val token_name = "token"
    val first_name = "first_name"
    val last_name = "last_name"
    val email = "email"
    val password = "password"
    var usersEmail = ""


    //Converrts long to date
    @TypeConverter
    fun toDate(dateLong: Long): Date {
        return dateLong?.let { Date(it) }
    }

    //Converts date to long
    @TypeConverter
    fun fromDate(date: Date): Long {
        return date.getTime()
    }
}