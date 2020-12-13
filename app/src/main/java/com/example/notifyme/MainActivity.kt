package com.example.notifyme

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*


private const val PRIMARY_CHANNEL_ID = "primary_notification_channel"
private const val NOTIFICATION_ID = 0
private const val ACTION_UPDATE_NOTIFICATION = "com.example.android.notifyme.ACTION_UPDATE_NOTIFICATION"

open class MainActivity() : AppCompatActivity() {
    private var notifyManager: NotificationManager? = null
    private val receiver = NotificationReceiver()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()
        registerReceiver(receiver, IntentFilter(ACTION_UPDATE_NOTIFICATION))

        notify.setOnClickListener {
            sendNotification()
        }
        update.setOnClickListener {
            updateNotification()
        }
        cancel.setOnClickListener {
            cancelNotification()
        }

        setNotificationButtonState(true, false, false)
    }

    fun updateNotification() {
        val androidImage = BitmapFactory.decodeResource(resources, R.drawable.mascot_1)

        val notifyBuilder = getNotificationBuilder()

        notifyBuilder?.setStyle(NotificationCompat.BigPictureStyle()
                .bigPicture(androidImage)
                .setBigContentTitle("Notification Updated!"))

        notifyManager?.notify(NOTIFICATION_ID, notifyBuilder?.build())

        setNotificationButtonState(false, false, true);
    }

    private fun cancelNotification() {
        notifyManager?.cancel(NOTIFICATION_ID)
        setNotificationButtonState(true, false, false);
    }

    private fun sendNotification() {
        val updateIntent = Intent(ACTION_UPDATE_NOTIFICATION)
        val updatePendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID, updateIntent, PendingIntent.FLAG_ONE_SHOT)
        val notifyBuilder = getNotificationBuilder()
        notifyBuilder?.addAction(R.drawable.ic_baseline_account_box_24, "Update Notification", updatePendingIntent)
        notifyManager?.notify(NOTIFICATION_ID, notifyBuilder?.build())
        setNotificationButtonState(false, true, true)
    }

    private fun getNotificationBuilder(): NotificationCompat.Builder? {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val notificationPendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        return NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle("You've been notified!")
                .setContentText("This is your notification text.")
                .setSmallIcon(R.drawable.ic_notif)
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true).setContentIntent(notificationPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
    }

    private fun setNotificationButtonState(isNotifyEnabled: Boolean,
                                           isUpdateEnabled: Boolean,
                                           isCancelEnabled: Boolean) {
        notify.isEnabled = isNotifyEnabled
        update.isEnabled = isUpdateEnabled
        cancel.isEnabled = isCancelEnabled
    }

    private fun createNotificationChannel() {
        notifyManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // Create a NotificationChannel
            val notificationChannel = NotificationChannel(PRIMARY_CHANNEL_ID,
                    "Mascot Notification", NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Notification from Mascot"
            notifyManager?.createNotificationChannel(notificationChannel)
        }
    }

    override fun onDestroy() {
        unregisterReceiver(receiver)
        super.onDestroy()
    }

    inner class NotificationReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            updateNotification()
        }
    }

}





