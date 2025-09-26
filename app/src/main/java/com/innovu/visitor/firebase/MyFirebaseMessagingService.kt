package com.innovu.visitor.firebase

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.GsonBuilder
import com.innovu.visitor.MainActivity
import com.innovu.visitor.utlis.StorePrefData
import timber.log.Timber

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val gson = GsonBuilder().setPrettyPrinting().create()
        Timber.tag("FCM").d("Data Payload JSON:\n${gson.toJson(remoteMessage.data)}")
        Timber.tag("FCM").d("Message Received from: ${remoteMessage.from}")
        val title = remoteMessage.notification?.title ?: "New Message"
        val body = remoteMessage.notification?.body ?: "You have a new notification"
        val data = remoteMessage.data
        if (isAppInForeground(applicationContext) && body.toString().toLowerCase().contains("visitor")) {
            // Send a local broadcast to show popup
            val intent = Intent("SHOW_CUSTOM_POPUP")
            intent.putExtra("title", title)
            intent.putExtra("message", body)
            intent.putExtra("id", data["id"])
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        }

        Timber.tag("FCM").d("Full Data Payload: $data")
        val screen = data["screen"]
        Timber.tag("FCM").d("Screen to open: $screen")
        showNotification(title, body,screen)
    }
    fun isAppInForeground(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses ?: return false
        val packageName = context.packageName
        for (appProcess in appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                appProcess.processName == packageName
            ) {
                return true
            }
        }
        return false
    }

    private fun showNotificationold(title: String, message: String,screen:String?) {
        val channelId = "default_channel_id"
        val channelName = "Default Channel"


        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra("openFragment", screen)  // 👈 Add this key to identify which fragment to open
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // TODO: Replace with your custom icon
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message)) // for longer messages
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android 8+
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            enableLights(true)
            enableVibration(true)
            setSound(soundUri, audioAttributes)
            description = "Channel for default notifications"
        }

        notificationManager.createNotificationChannel(channel)

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())





    }


    private fun showNotification(title: String, message: String, screen: String?) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("openFragment", screen)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, "default_channel_id")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel("default_channel_id", "Default Channel", NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)

        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.tag("FCM").d("Refreshed token: $token")
        // Save or send token to your backend server
        StorePrefData.token = token
    }
}
