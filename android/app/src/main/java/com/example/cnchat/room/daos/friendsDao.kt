package com.example.cnchat.room.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.cnchat.room.models.friendsTable
import com.example.cnchat.room.models.messageTable

@Dao
interface friendsDao {

    /**
     * This function will be used when the user is using the app to constantly update the changed made to the database
     */
    @Query("SELECT * FROM friendsTable ORDER BY date DESC")
    fun getWholeList(): LiveData<List<friendsTable>>

    /**
     * Since when the app is in background state, we only need the values once to check if user exist or not
     */
    @Query("SELECT * FROM friendsTable WHERE friendsEmail=:email")
    fun ifUserExists(email : String) : List<friendsTable>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(friend: friendsTable)

    @Update
    suspend fun update(friend: friendsTable)

    @Query("DELETE FROM friendsTable")
    suspend fun deleteAll()
}