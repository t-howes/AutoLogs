package sample.thowes.autoservice.notifications

import sample.thowes.autoservice.R.mipmap.ic_launcher
import android.app.AlarmManager
import android.app.Notification
import android.content.Context.ALARM_SERVICE
import android.os.SystemClock
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import sample.thowes.autoservice.R
import java.util.*


class ReminderService {

  companion object {

    fun scheduleNotification(context: Context, notification: Notification, delay: Int) {
      val calendar = Calendar.getInstance()
      calendar.add(Calendar.SECOND, delay)

      val notificationIntent = Intent(context, NotificationPublisher::class.java)
      notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1)
      notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification)
      val pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

      val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
      alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }

    fun getNotification(context: Context, content: String): Notification {
      val builder = Notification.Builder(context)
      builder.setContentTitle("Scheduled Notification")
      builder.setContentText(content)
      builder.setSmallIcon(R.mipmap.ic_launcher_round)
      return builder.build()
    }
  }
}