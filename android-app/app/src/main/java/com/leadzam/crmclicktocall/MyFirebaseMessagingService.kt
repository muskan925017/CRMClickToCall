
package com.leadzam.crmclicktocall

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val CHANNEL_ID = "call_requests"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_TOKEN", "New token received: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val phoneNumber = message.data["phoneNumber"]
        val contactName = message.data["contactName"] ?: "Unknown contact"

        if (phoneNumber.isNullOrBlank()) {
            Log.e("FCM_CALL", "Missing phone number")
            return
        }

        Log.d("FCM_CALL", "Call request received for $contactName")

        createNotificationChannel()
        showCallNotification(contactName, phoneNumber)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "CRM Call Requests",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for incoming CRM call requests"
            }

            val manager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            manager.createNotificationChannel(channel)
        }
    }

    private fun showCallNotification(
        contactName: String,
        phoneNumber: String
    ) {
        val dialIntent = Intent(
            Intent.ACTION_DIAL,
            Uri.fromParts("tel", phoneNumber, null)
        )

        val pendingIntent = PendingIntent.getActivity(
            this,
            phoneNumber.hashCode(),
            dialIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("CRM call request: $contactName")
            .setContentText("Number: $phoneNumber")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Call request from LeadZam CRM\n$contactName\n$phoneNumber")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setAutoCancel(true)
            .addAction(
                0,
                "Call",
                pendingIntent
            )
            .build()

        try {
            NotificationManagerCompat.from(this).notify(
                System.currentTimeMillis().toInt(),
                notification
            )
        } catch (exception: SecurityException) {
            Log.e("FCM_CALL", "Notification permission not granted", exception)
        }
    }
}
