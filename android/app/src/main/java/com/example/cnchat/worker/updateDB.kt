package com.example.cnchat.worker

import android.content.Context
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.cnchat.constants
import com.example.cnchat.room.models.friendsTable
import com.example.cnchat.room.models.messageTable
import com.example.cnchat.room.myRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.jetbrains.anko.runOnUiThread
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Handler


class updateDBWorker(val appContext: Context, workerParams: WorkerParameters): Worker(appContext, workerParams) {


    //Since the work in work manager by default happens in background thread. Therefore i didn't use coroutine here
    override fun doWork(): Result {

        // Indicate whether the work finished successfully with the Result
        try {
            val message = inputData.getString("message")!!
            val sendersEmail = inputData.getString("sendersEmail")!!
            val sendersfirstName = inputData.getString("sendersfirstName")!!
            val senderslastName = inputData.getString("senderslastName")!!
            val recipientsEmail = inputData.getString("recipientsEmail")!!
            val recipientsfirstName = inputData.getString("recipientsfirstName")!!
            val recipientslastName = inputData.getString("recipientslastName")!!
            val timeOfMessage = inputData.getString("timeOfMessage")!!
            val dateOfMessage = inputData.getString("dateOfMessage")!!

            //Setting up the database
            val applicationScope = CoroutineScope(SupervisorJob())
            val database = myRoomDatabase.getDatabase(appContext, applicationScope)
            val msgDao = database.msgDao()
            val frndsDao = database.frndsDao()

            //First insert the message in the table through db
            msgDao.insertThroughWorker(
                    messageTable(
                            message = message,
                            sendersEmail = sendersEmail,
                            recipientsEmail = recipientsEmail,
                            dateofmessaging = dateOfMessage,
                            timeofmessaging = timeOfMessage,
                            sendersFirstName = sendersfirstName,
                            sendersLastName = senderslastName,
                            recipientsFirstName = recipientsfirstName,
                            recipientsLastName = recipientslastName
                    )
            )

            //Now if message is inserted, then also update the last message exchanged with the user

            /**
             * You can use @Insert(onConflict = OnConflictStrategy.REPLACE). This will try to insert the entity and, if there is an existing row that has the same ID value,
             * it will delete it and replace it with the entity you are trying to insert. Be aware that, if you are using auto generated IDs,
             * this means that the the resulting row will have a different ID than the original that was replaced.
             */
            frndsDao.insertThroughWorker(
                    friendsTable(
                            lastMessageExchanged = message,
                            friendsEmail = sendersEmail,
                            dateOfMessage = dateOfMessage,
                            timeOfMessage = timeOfMessage,
                            friendsFirstName = sendersfirstName,
                            friendslastName = senderslastName
                    )
            )
            return Result.success()
        }catch (e : Exception){
            Log.i("error ------",e.message.toString())
            Toast.makeText(applicationContext,"error - " + e.printStackTrace(),Toast.LENGTH_LONG).show()
            return Result.failure()
        }
    }
}