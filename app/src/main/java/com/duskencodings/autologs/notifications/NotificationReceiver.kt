package com.duskencodings.autologs.notifications

import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.duskencodings.autologs.models.Reminder
import com.duskencodings.autologs.utils.log.Logger
import java.util.*

class NotificationReceiver : BroadcastReceiver() {

  override fun onReceive(context: Context, intent: Intent) {
    val notification = intent.getParcelableExtra<Notification>(NOTIFICATION)
    val reminder = intent.getParcelableExtra<Reminder>(EXTRA_REMINDER)
    val id = intent.getIntExtra(NOTIFICATION_ID, 0)
    val action = intent.action
    Logger.i("NotificationReceiver", "Received notification: $notification")
    Logger.i("NotificationReceiver", "Received reminder: $reminder")
    Logger.i("NotificationReceiver", "Intent action: $action")

    if (notification != null) {
      NotificationService.publishNotification(context, notification, id)
    } else if (action == ACTION_SNOOZE && reminder != null) {
      NotificationService.cancelNotification(context, id)
      NotificationService.scheduleNotification(context, reminder, id, Calendar.getInstance().apply { add(Calendar.DATE, 1) })
    }
  }

  companion object {
    const val NOTIFICATION_ID = "notification-id"
    const val NOTIFICATION = "notification"
    const val EXTRA_REMINDER = "extra_reminder"
    const val ACTION_SNOOZE = "action_snooze"
  }
}