package com.example.cnchat.room.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messageTable")
class messageTable(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val text : String,
    val sender : String,
    val recipient : String,
    val dateofmessaging : String,
    val timeofmessaging : String
)