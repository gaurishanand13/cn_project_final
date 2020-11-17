package com.example.cnchat

import android.app.Application
import com.example.cnchat.repositary.messageRepositary
import com.example.cnchat.room.myRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class myApplicationClass : Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { myRoomDatabase.getDatabase(this,applicationScope) }
    val repository by lazy { messageRepositary(database.msgDao(),database.frndsDao()) }
}