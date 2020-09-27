package com.duskencodings.autologs.notifications

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.duskencodings.autologs.dagger.injector.Injector
import com.duskencodings.autologs.models.Reminder
import com.duskencodings.autologs.repo.RemindersRepository
import com.duskencodings.autologs.utils.log.Logger
import com.duskencodings.autologs.utils.now
import javax.inject.Inject

class NotificationReceiver : BroadcastReceiver() {

  @Inject
  lateinit var reminderRepo: RemindersRepository

  init {
    Injector.component.inject(this)
  }

  override fun onReceive(context: Context, intent: Intent) {
    val notification = intent.getParcelableExtra<Notification>(NOTIFICATION)
    val reminder = intent.getParcelableExtra<Reminder>(EXTRA_REMINDER)
    val id = intent.getIntExtra(NOTIFICATION_ID, 0)
    val action = intent.action
    Logger.i(TAG, "Received notification: $notification")
    Logger.i(TAG, "Received reminder: $reminder")
    Logger.i(TAG, "Intent action: $action")

    if (notification != null) {
      NotificationService.publishNotification(context, notification, id)
    } else if (action == ACTION_SNOOZE && reminder != null) {
      NotificationService.cancelNotification(context, id)
      reminderRepo.saveReminderDate(reminder.id!!, now().plusDays(7)).subscribe({}, { error ->
        Logger.e(TAG, "failed to update reminder expiration", error)
      })
    }
  }

  companion object {
    const val TAG = "NotificationReceiver"
    const val NOTIFICATION_ID = "notification-id"
    const val NOTIFICATION = "notification"
    const val EXTRA_REMINDER = "extra_reminder"
    const val ACTION_SNOOZE = "action_snooze"
  }
}