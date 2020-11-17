package com.example.cnchat.room.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Time
import java.util.*

@Entity(tableName = "friendsTable")
class friendsTable(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var lastMessageExchanged : String,
    var friendsEmail : String,
    var date: Long
)