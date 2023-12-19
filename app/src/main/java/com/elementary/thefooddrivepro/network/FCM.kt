package com.elementary.thefooddrivepro.network

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.elementary.thefooddrivepro.MainActivity
import com.elementary.thefooddrivepro.R
import com.elementary.thefooddrivepro.Splash
import com.elementary.thefooddrivepro.auth.Login
import com.elementary.thefooddrivepro.chat.ChatFragment
import com.elementary.thefooddrivepro.utils.AppController
import com.elementary.thefooddrivepro.utils.Constants
import com.elementary.thefooddrivepro.utils.SharedPreference
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject
import kotlin.random.Random

class FCM : FirebaseMessagingService() {

    companion object {
        private val TAG = FCM::class.java.simpleName
    }

    override fun onNewToken(s: String) {
        super.onNewToken(s)
    }

    private fun checkRunningServices(serviceClass: Class<*>): Boolean {
        val manager =
            getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(
            Int.MAX_VALUE
        )) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "onMessageReceived: $remoteMessage")

        var title = ""
        var body = ""

        try {
            if (remoteMessage.data.isNotEmpty()) {
                val data: JSONObject = JSONObject(remoteMessage.data.toString())

                val notificationData = data.getJSONObject("notification")
                val obj = notificationData.getJSONObject("obj")

                title = notificationData.getString("title")
                body = notificationData.getString("body")

                val messageData = obj.getJSONObject("message")
                messageData.put("donation_type", obj.getString("donation_type"))

                if (ChatFragment.isChatActive) {

                    if (ChatFragment.donationId == messageData.getInt("donation_id")) {
                        val intent = Intent("ChatBroadCast")
                        intent.putExtra("messageData", messageData.toString())
                        intent.putExtra("fullImage", obj.getString("full_image"))
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                    } else {
                        showNotification(title, body, messageData)
                    }
                } else {
                    showNotification(title, body, messageData)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        Log.d("", "")
    }

    private fun showNotification(
        title: String,
        body: String,
        messageData: JSONObject
    ) {

        var resultIntent: Intent? = null

        resultIntent =
            if (AppController.isAppRunning && SharedPreference.getBoolean(
                    this,
                    Constants.IsLoggedIn
                )
            ) {
                Intent(this, MainActivity::class.java)

            } else if (!SharedPreference.getBoolean(
                    this,
                    Constants.IsLoggedIn
                ) && AppController.isAppRunning
            ) {
                Intent(this, Login::class.java)
            } else {
                Intent(this, Splash::class.java)
            }


        resultIntent.putExtra("isComingFromNotification", true)
        resultIntent.putExtra("messageData", messageData.toString())


        resultIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        val resultPendingIntent = PendingIntent.getActivity(
            applicationContext,
            Random.nextInt(),
            resultIntent,
            0
        )


        val mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        var notificationChannel: NotificationChannel? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(
                Constants.channelName, Constants.channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.description = ""
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            mNotificationManager.createNotificationChannel(notificationChannel)
        }

        // to display notification in DND Mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                mNotificationManager.getNotificationChannel(Constants.channelName)
            channel.canBypassDnd()
        }

        val notificationBuilder =
            NotificationCompat.Builder(this, Constants.channelName)

        notificationBuilder
            .setContentTitle(title)
            .setContentText(body)
            .setGroup(Constants.channelName)
            .setStyle(NotificationCompat.BigTextStyle())
            .setDefaults(Notification.DEFAULT_ALL)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.ic_logo_inner)
            .setAutoCancel(true)
            .setGroupSummary(true)
            .setContentIntent(resultPendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationChannel != null) {
                notificationBuilder.setChannelId(Constants.channelName)
                mNotificationManager.createNotificationChannel(notificationChannel)
            }
        }
        mNotificationManager.notify(
            System.currentTimeMillis().toInt(),
            notificationBuilder.build()
        )
    }
}