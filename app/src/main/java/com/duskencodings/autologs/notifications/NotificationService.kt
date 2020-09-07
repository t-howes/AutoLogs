package com.duskencodings.autologs.notifications

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.duskencodings.autologs.R
import com.duskencodings.autologs.models.Reminder
import java.util.Calendar

class NotificationService {

  companion object {

    fun scheduleNotification(context: Context, reminder: Reminder, delivery: Calendar) {
      scheduleNotification(context, getNotification(context, reminder), reminder.currentMiles, delivery)
    }

    private fun scheduleNotification(context: Context, notification: Notification, notificationId: Int, delivery: Calendar) {
      // Send an intent to trigger the NotificationReceiver that will publish the notification.
      val notificationIntent = Intent(context, NotificationReceiver::class.java).apply {
        putExtra(NotificationReceiver.NOTIFICATION_ID, notificationId)
        putExtra(NotificationReceiver.NOTIFICATION, notification)
      }
      val pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

      val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
      alarmManager.set(AlarmManager.RTC_WAKEUP, delivery.timeInMillis, pendingIntent)
    }

    fun publishNotifications(context: Context, reminders: List<Reminder>) {
      reminders.forEach { publishNotification(context, getNotification(context, it), it.id!!) }
    }

    fun publishNotification(context: Context, notification: Notification, notificationId: Int) {
      val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      notificationManager.notify(notificationId, notification)
    }

    private fun getNotification(context: Context, reminder: Reminder): Notification {
      return Notification.Builder(context, reminder.name).apply {
        setContentTitle("Service Reminder")
        setContentText(reminder.pushNotificationText())
        setSmallIcon(R.mipmap.ic_launcher_round)
      }.build()
    }
  }
}