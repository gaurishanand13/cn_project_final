package com.example.cnchat.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.cnchat.R
import com.example.cnchat.constants
import com.example.cnchat.retrofit.retrofitClient
import com.example.cnchat.room.models.friendsTable
import com.example.cnchat.room.models.messageTable
import com.example.cnchat.room.myRoomDatabase
import com.example.cnchat.worker.updateDBWorker
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.*
import org.jetbrains.anko.runOnUiThread
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

import kotlin.random.Random


class MyFirebaseInstanceService : FirebaseMessagingService() {
    var url = "https://cse.sc.edu/~sur/csce416_f19/csce416_banner.png"

    override fun onNewToken(s: String) {
        super.onNewToken(s)

        //Update this FCM token on server too
        val sPref = applicationContext.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE)
        constants.token = sPref.getString(constants.token_name, "")!!
        /**
         * FCM token to the device may be assigned just after the app in installed , user may not have still logged in. So we should make sure that user
         * has logged in before updating the FCM token on server
         */
        if(!constants.token.isEmpty()){
            //Though retrofit updated the token on the server
            retrofitClient.retrofitService.updateFCMToken(constants.bearer + constants.token, s)
        }
        else{
            //User is not logged in, so doesn't perform this operation.
        }

    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        showNotification(remoteMessage.data)
    }

    private fun showNotification(data: Map<String, String>) {

        val message = data["message"].toString()
        val sendersEmail = data["sendersEmail"].toString()
        val sendersfirstName = data["sendersfirstName"].toString()
        val senderslastName = data["senderslastName"].toString()
        val recipientsEmail = data["recipientsEmail"].toString()
        val recipientsfirstName = data["recipientsfirstName"].toString()
        val recipientslastName = data["recipientslastName"].toString()
        val timeOfMessage = data["timeOfMessage"].toString()
        val dateOfMessage = data["dateOfMessage"].toString()

        //Title of the notification
        val title = "${sendersfirstName} ${senderslastName} sent you a message"




        //Also save this data in the room database so that data get updated in the app too. Saving the data in room using work manager.
        // We could have done using coroutines too. But i will be doing like this.
        val workerRequest = OneTimeWorkRequestBuilder<updateDBWorker>()

        //Setting the input data for the worker
        val data = Data.Builder()
        data.putString("message",message)
        data.putString("sendersEmail",sendersEmail)
        data.putString("sendersfirstName",sendersfirstName)
        data.putString("senderslastName",senderslastName)
        data.putString("recipientsEmail",recipientsEmail)
        data.putString("recipientsfirstName",recipientsfirstName)
        data.putString("recipientslastName",recipientslastName)
        data.putString("timeOfMessage",timeOfMessage)
        data.putString("dateOfMessage",dateOfMessage)
        workerRequest.setInputData(data.build())

        //Making a request to the work manager
        WorkManager.getInstance(this).enqueue(workerRequest.build())


        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val NOTIFICATION_CHANNEL_ID = packageName
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID, "Notification",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.description = "Team Codeline"
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.BLUE
            notificationChannel.enableLights(true)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(
            this,
            NOTIFICATION_CHANNEL_ID
        )
        notificationBuilder.setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.add)
            .setContentTitle(title)
            .setContentText(message)
            .setLargeIcon(getBitmapFromURL(url))
            .setStyle(NotificationCompat.BigTextStyle())
            .setContentInfo("Info")
        notificationManager.notify(Random.nextInt(), notificationBuilder.build())
    }

    companion object {

        fun getBitmapFromURL(src: String?): Bitmap? {
            return try {
                val url = URL(src)
                val connection = url.openConnection() as HttpURLConnection
                connection.setDoInput(true)
                connection.connect()
                val input = connection.getInputStream()
                BitmapFactory.decodeStream(input)
            } catch (e: IOException) {
                Log.i("exception in the image", e.message.toString())
                null
            }
        }
    }
}