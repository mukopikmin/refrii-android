package app.muko.mypantry.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.os.Build
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.core.app.NotificationCompat
import app.muko.mypantry.R
import app.muko.mypantry.data.models.User
import app.muko.mypantry.data.source.ApiUserRepository
import app.muko.mypantry.foodlist.FoodListActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.android.AndroidInjection
import io.reactivex.subscribers.DisposableSubscriber
import javax.inject.Inject


class PushNotificationService : FirebaseMessagingService() {

    private lateinit var mPreference: SharedPreferences

    @Inject
    lateinit var mApiUserRepository: ApiUserRepository

    override fun onCreate() {
        super.onCreate()

        AndroidInjection.inject(this)
//        (application as App).getComponent().inject(this)

        mPreference = PreferenceManager.getDefaultSharedPreferences(applicationContext)
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)

        register(p0)
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        val title = p0.notification?.title
        val body = p0.notification?.body

        sendNotification(title, body)
    }

    private fun register(token: String?) {
        token ?: return

        val userId = mPreference.getInt(application.getString(R.string.preference_key_id), 0)

        mApiUserRepository.registerPushToken(userId, token)
                .subscribe(object : DisposableSubscriber<User>() {
                    override fun onNext(t: User?) {
                        val editor = mPreference.edit()

                        editor.putString(application.getString(R.string.preference_key_push_token), token)
                        editor.apply()
                    }

                    override fun onError(t: Throwable?) {
                        Toast.makeText(applicationContext, t?.message, Toast.LENGTH_LONG).show()
                    }

                    override fun onComplete() {}

                })
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