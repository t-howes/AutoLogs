package com.duskencodings.autologs.notifications

import android.app.*
import android.app.Notification.EXTRA_NOTIFICATION_ID
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.duskencodings.autologs.R
import com.duskencodings.autologs.models.Reminder
import com.duskencodings.autologs.notifications.NotificationReceiver.Companion.ACTION_SNOOZE
import com.duskencodings.autologs.notifications.NotificationReceiver.Companion.EXTRA_REMINDER
import com.duskencodings.autologs.utils.notificationManager
import com.duskencodings.autologs.views.cars.details.CarDetailsActivity
import com.duskencodings.autologs.views.maintenance.details.CarWorkDetailsActivity
import java.util.*

object NotificationService {
  fun publishNotifications(context: Context, reminders: List<Reminder>) {
    reminders.forEachIndexed { index, reminder -> publishNotification(context, reminder, index) }
  }

  private fun publishNotification(context: Context, reminder: Reminder, notificationId: Int) {
    val notification = getNotification(context, reminder, notificationId)
    createNotificationChannel(context, reminder.name)
//    scheduleNotification(context, notification, reminder.id!!.toInt(), delivery)
    publishNotification(context, notification, notificationId)
  }

  fun scheduleNotification(context: Context, reminder: Reminder, notificationId: Int, delivery: Calendar = Calendar.getInstance().apply { add(Calendar.SECOND, 5) }) {
    val notification = getNotification(context, reminder, notificationId)
    scheduleNotification(context, notification, notificationId, delivery)
  }

  private fun scheduleNotification(context: Context, notification: Notification, notificationId: Int, delivery: Calendar = Calendar.getInstance().apply { add(Calendar.SECOND, 10) }) {
    // Send an intent to trigger the NotificationReceiver that will publish the notification.
    val notificationIntent = getNotificationIntent(context, notificationId, notification)
    val pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.set(AlarmManager.RTC_WAKEUP, delivery.timeInMillis, pendingIntent)
  }

  private fun getNotificationIntent(context: Context, notificationId: Int, notification: Notification): Intent {
    return Intent(context, NotificationReceiver::class.java).apply {
      putExtra(NotificationReceiver.NOTIFICATION_ID, notificationId)
      putExtra(NotificationReceiver.NOTIFICATION, notification)
    }
  }

  fun publishNotification(context: Context, notification: Notification, notificationId: Int) {
    context.notificationManager().notify(notificationId, notification)
  }

  fun cancelNotification(context: Context, notificationId: Int) {
    context.notificationManager().cancel(notificationId)
  }

  private fun createNotificationChannel(context: Context, name: String) {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val importance = NotificationManager.IMPORTANCE_DEFAULT
      val channel = NotificationChannel(name, name, importance)
      context.notificationManager().createNotificationChannel(channel)
    }
  }

  private fun getNotification(context: Context, reminder: Reminder, notificationId: Int): Notification {
    val snoozeIntent = Intent(context, NotificationReceiver::class.java).apply {
      action = ACTION_SNOOZE
      putExtra(EXTRA_NOTIFICATION_ID, notificationId)
      putExtra(EXTRA_REMINDER, reminder)
    }
    val snoozePendingIntent = PendingIntent.getBroadcast(context, 0, snoozeIntent, 0)
    val snoozeAction = NotificationCompat.Action.Builder(
        android.R.drawable.ic_lock_silent_mode, context.getString(R.string.remind_me_tomorrow), snoozePendingIntent
      ).build()
    val workDetailsIntent = CarWorkDetailsActivity.newIntent(context, reminder.carId, previousWorkId = reminder.carWorkId)
    val detailsPendingIntent = PendingIntent.getBroadcast(context, 0, workDetailsIntent, 0)
    val completeAction = NotificationCompat.Action.Builder(
        android.R.drawable.ic_menu_save, context.getString(R.string.done_this), detailsPendingIntent
      ).build()

    return NotificationCompat.Builder(context, reminder.name)
      .setContentTitle("${reminder.name} Reminder")
      .setContentText(reminder.pushNotificationText())
      .setSmallIcon(R.drawable.ic_launcher_foreground)
      .setColor(ContextCompat.getColor(context, R.color.colorAccent))
      .setContentIntent(notificationClickIntent(context, reminder))
      .addAction(snoozeAction)
      .addAction(completeAction)
      .setAutoCancel(true)
      .build()
  }

  private fun notificationClickIntent(context: Context, reminder: Reminder): PendingIntent {
    val carId = reminder.carId
    val carDetailsIntent = CarDetailsActivity.newIntent(context, carId)
    return TaskStackBuilder.create(context).run {
      addNextIntentWithParentStack(carDetailsIntent)
      getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
    }
  }
}