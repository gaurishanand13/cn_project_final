package com.example.cnchat.room.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Time
import java.util.*


/**
 * Note basically date is stored in the long format as room can't store date directly in the local storage. Therefore we converted first date to long and then stored it.
 */

//I have made the friendsEmail as a primary key because everytime last message exchanged with some user will be unique, therefore for each email entry will be unique
@Entity(tableName = "friendsTable")
class friendsTable(
    @PrimaryKey var friendsEmail : String,
    var friendsFirstName : String,
    var friendslastName : String,
    var lastMessageExchanged : String,
    var dateOfMessage: String,
    var timeOfMessage: String
)