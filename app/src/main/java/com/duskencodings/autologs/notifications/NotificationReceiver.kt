package com.duskencodings.autologs.notifications

import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {

  override fun onReceive(context: Context, intent: Intent) {
    val notification = intent.getParcelableExtra<Notification>(NOTIFICATION)
    val id = intent.getIntExtra(NOTIFICATION_ID, 0)

    notification?.let {
      NotificationService.publishNotification(context, notification, id)
    }
  }

  companion object {
    const val NOTIFICATION_ID = "notification-id"
    const val NOTIFICATION = "notification"
  }
}