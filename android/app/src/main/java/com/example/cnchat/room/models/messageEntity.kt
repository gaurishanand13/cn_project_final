package com.example.cnchat.room.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "messageTable")
class messageTable(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val message : String,
    val sendersEmail : String,
    val recipientsEmail : String,
    var sendersFirstName: String,
    var sendersLastName: String,
    var recipientsFirstName : String,
    var recipientsLastName : String,
    val dateofmessaging : String,
    val timeofmessaging : String
)