package com.refrii.client.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.refrii.client.R
import com.refrii.client.foodlist.FoodListActivity


class PushNotificationService : FirebaseMessagingService() {

    override fun onNewToken(newToken: String?) {
        super.onNewToken(newToken)

        FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w(TAG, "getInstanceId failed", task.exception)
                        return@OnCompleteListener
                    }

                    val token = task.result?.token

                    Log.d(TAG, token)
                })

        Log.d(TAG, newToken)
    }

    override fun onMessageReceived(message: RemoteMessage?) {
        super.onMessageReceived(message)

        val title = message?.notification?.title
        val body = message?.notification?.body

        sendNotification(title, body)
    }

    private fun sendNotification(title: String?, message: String?) {
        val intent = Intent(this, FoodListActivity::class.java)

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        val notificationBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setSubText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setStyle(NotificationCompat.BigTextStyle().bigText(title))
                .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder.setChannelId(getString(R.string.notification_channel_id_push))
        }

        notificationManager.notify(0, notificationBuilder.build())
    }

    companion object {
        private const val TAG = "PushNotificationService"
    }
}