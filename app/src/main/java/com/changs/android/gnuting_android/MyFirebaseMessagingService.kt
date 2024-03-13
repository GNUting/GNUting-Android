package com.changs.android.gnuting_android

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.changs.android.gnuting_android.ui.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*

class MyFirebaseMessagingService: FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        //수신한 메시지를 처리
        val title = message.notification?.title
        val body = message.notification?.body
        sendNotification(title, body)
    }

    private fun sendNotification(title: String?, body: String?) {
        val intent = Intent(this, MainActivity::class.java)
        val pIntent = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val channelId = getString(R.string.channel_id)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(title)
            .setContentText(body)
            .setContentIntent(pIntent)
            .setAutoCancel(true)

        getSystemService(NotificationManager::class.java).run {
            val channel = NotificationChannel(channelId, "알림", NotificationManager.IMPORTANCE_HIGH)
            createNotificationChannel(channel)

            notify(Date().time.toInt(), notificationBuilder.build())
        }
    }
}