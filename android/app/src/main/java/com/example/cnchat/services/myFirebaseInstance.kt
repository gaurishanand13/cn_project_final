package com.example.cnchat.services

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.cnchat.MainActivity
import com.example.cnchat.constants
import com.example.cnchat.myApplicationClass
import com.example.cnchat.retrofit.model.message
import com.example.cnchat.retrofit.retrofitClient
import com.example.cnchat.room.models.friendsTable
import com.example.cnchat.room.models.messageTable
import com.example.cnchat.viewModel.messageViewModel
import com.example.cnchat.viewModel.messageViewModelFactory
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
        val sPref = this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE)
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
        val sendersEmail = data["sendersEmail"].toString()
        val title = data["title"].toString()
        val receipent = data["receipent"].toString()
        val body = data["body"].toString()
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
            .setSmallIcon(R.drawable.ic_input_add)
            .setContentTitle(title)
            .setContentText(body)
            .setLargeIcon(getBitmapFromURL(url))
            .setStyle(NotificationCompat.BigTextStyle())
            .setContentInfo("Info")
        notificationManager.notify(Random.nextInt(), notificationBuilder.build())

        //Also save this data in the room database so that data get updated in the app too.
        updateDataInRoom(sendersEmail,body,receipent)
    }

    fun updateDataInRoom(sendersEmail: String, message: String,receipent: String){

        //First get the current time
        val c: Date = Calendar.getInstance().getTime()

        GlobalScope.launch {
            val ans = (application as myApplicationClass).repository.isUserExists(
                sendersEmail
            )
            if(ans.size==0){
                //Then insert the user
                (application as myApplicationClass).repository.insertUser(
                    friendsTable(lastMessageExchanged = message, friendsEmail= sendersEmail, date = constants.fromDate(c))
                )
            }
            else{
                //Otherwise update the user in the room database
                ans[0].lastMessageExchanged = message
                ans[0].date = constants.fromDate(c)
                (application as myApplicationClass).repository.updateUser(ans[0])
            }

            //Setting the date
            var postFormater = SimpleDateFormat("MMMM dd, yyyy")
            val newDateStr: String = postFormater.format(c)

            //"hh:mm a"
            postFormater = SimpleDateFormat("hh:mm a")
            val time = postFormater.format(c)


            (application as myApplicationClass).repository.insertMessage(
                messageTable(
                    text = message,
                    sender = sendersEmail,
                    recipient = receipent,
                    dateofmessaging = newDateStr,
                    timeofmessaging = time
                )
            )
        }
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