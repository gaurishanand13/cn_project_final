package com.example.cnchat.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.cnchat.room.daos.friendsDao
import com.example.cnchat.room.daos.messageDao
import com.example.cnchat.room.models.friendsTable
import com.example.cnchat.room.models.messageTable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@Database(entities = arrayOf(messageTable::class, friendsTable::class), version = 1, exportSchema = false)
abstract class myRoomDatabase : RoomDatabase() {

    //Insert all the daos here for the room database -
    abstract fun msgDao(): messageDao
    abstract fun frndsDao() : friendsDao

    private class myRoomDatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            /**
             * This method will be executed for the first time, when database is opened i.e when app is opened for the first time i.e it
             * won't be executed every time
             */
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    //Deleting all the contents in all the tables if exists for this app.
                    var wordDao = database.msgDao()
                    // Delete all content here.
                    wordDao.deleteAll()

                    var friendsDao = database.frndsDao()
                    friendsDao.deleteAll()
                }
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: myRoomDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): myRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    myRoomDatabase::class.java,
                    "word_database"
                )
                    .addCallback(myRoomDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}