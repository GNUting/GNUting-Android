package com.changs.android.gnuting_android

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.changs.android.gnuting_android.ui.HomeActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.Date

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val title = message.notification?.title
        val body = message.notification?.body
        val location = message.data["location"]

        sendNotification(title, body, location)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun sendNotification(title: String?, body: String?, location: String?) {
        if (GNUApplication.isActiveChatFragment) return

        val intent = Intent(this, HomeActivity::class.java)

        if (location != null) {
            intent.putExtra("location", location)
        }

        val pIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
        } else {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val channelId = getString(R.string.channel_id)

        val notificationBuilder =
            NotificationCompat.Builder(this, channelId).setColor(getColor(R.color.main))
                .setPriority(NotificationCompat.PRIORITY_HIGH).setSmallIcon(R.drawable.ic_fcm_logo)
                .setContentTitle(title).setContentText(body).setContentIntent(pIntent)
                .setAutoCancel(true).setStyle(NotificationCompat.BigTextStyle())

        getSystemService(NotificationManager::class.java).run {
            val channel = NotificationChannel(channelId, "알림", NotificationManager.IMPORTANCE_HIGH)
            createNotificationChannel(channel)

            notify(Date().time.toInt(), notificationBuilder.build())
        }
    }
}