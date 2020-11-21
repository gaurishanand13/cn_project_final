package com.example.cnchat.room.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cnchat.room.models.messageTable


@Dao
interface messageDao {

    @Query("SELECT * FROM messageTable WHERE sendersEmail=:friend OR recipientsEmail=:friend")
    fun getAllMessagesOfSpecificUser(friend: String?): LiveData<List<messageTable>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: messageTable)

    @Query("DELETE FROM messageTable")
    suspend fun deleteAll()

    //----------------------------------------
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertThroughWorker(user: messageTable)
}