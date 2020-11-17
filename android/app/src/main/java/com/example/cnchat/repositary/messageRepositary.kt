package com.example.cnchat.repositary

import android.provider.SyncStateContract.Helpers.insert
import android.telecom.Call
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.cnchat.constants
import com.example.cnchat.retrofit.model.fcmTokenResponse
import com.example.cnchat.retrofit.retrofitClient
import com.example.cnchat.room.daos.friendsDao
import com.example.cnchat.room.daos.messageDao
import com.example.cnchat.room.models.friendsTable
import com.example.cnchat.room.models.messageTable

class messageRepositary(val msgDao: messageDao,val frndsDao : friendsDao) {


    val allUsers: LiveData<List<friendsTable>> = frndsDao.getWholeList()

    /**
     *  By default Room runs suspend queries off the main thread, therefore, we don't need to implement anything else to ensure we're not doing long running database work
     *  off the main thread.
     */
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertMessage(message: messageTable) {
        msgDao.insert(message)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertUser(friend : friendsTable){
        frndsDao.insert(friend)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateUser(friend : friendsTable){
        frndsDao.update(friend)
    }


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun isUserExists(email : String) : List<friendsTable>{
        return frndsDao.ifUserExists(email)
    }

    /**
     * This will be used to maintain the messages of current chat opened.
     */
    fun getCurrentMessagesLiveData(friendsUserName : String) :  LiveData<List<messageTable>> {
        val allMessages: LiveData<List<messageTable>> = msgDao.getAllMessagesOfSpecificUser(friendsUserName)
        return allMessages
    }
}