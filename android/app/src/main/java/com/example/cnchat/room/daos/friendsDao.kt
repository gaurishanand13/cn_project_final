package com.example.cnchat.room.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.cnchat.room.models.friendsTable
import com.example.cnchat.room.models.messageTable

@Dao
interface friendsDao {

    /**
     * This function will be used when the user is using the app to constantly update the changed made to the database for the home adapter
     * also get these messages in descending order of tim
     */
    @Query("SELECT * FROM friendsTable")
    fun getWholeList(): LiveData<List<friendsTable>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(friend: friendsTable)


    @Query("DELETE FROM friendsTable")
    suspend fun deleteAll()

    //----------------------------------------------------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertThroughWorker(friend: friendsTable)








}