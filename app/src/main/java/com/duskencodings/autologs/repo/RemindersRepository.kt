package com.duskencodings.autologs.repo

import android.content.Context
import androidx.lifecycle.LiveData
import com.duskencodings.autologs.base.BaseRepository
import com.duskencodings.autologs.database.RemindersDb
import com.duskencodings.autologs.models.CarWork
import com.duskencodings.autologs.models.Preference
import com.duskencodings.autologs.models.Reminder
import com.duskencodings.autologs.models.ReminderType
import com.duskencodings.autologs.notifications.NotificationService
import io.reactivex.Single
import java.util.*

class RemindersRepository(
    context: Context,
    private val remindersDb: RemindersDb
) : BaseRepository(context) {

  fun getAllReminders(): Single<List<Reminder>> = remindersDb.getAllReminders()
  fun getReminders(carId: Long): Single<List<Reminder>> = remindersDb.getReminders(carId)
  private fun getReminder(carWorkId: Long): Single<Reminder> = remindersDb.getByCarWork(carWorkId)
  private fun saveReminder(reminder: Reminder): Reminder = remindersDb.insertOrUpdate(reminder)
  fun getLiveUpcomingReminders(carId: Long): LiveData<List<Reminder>> = remindersDb.getLiveUpcomingReminders(carId)
  fun getNotificationReminders(): Single<List<Reminder>> = remindersDb.getNotificationReminders()

  fun saveOrCreateReminder(carWork: CarWork, pref: Preference): Single<Reminder> {
    return getReminder(carWork.id!!)
      .map { existingReminder ->
        // update the existing Reminder with new expiration fields
        saveReminder(existingReminder.copy(
            expireAtMiles = carWork.miles + pref.miles,
            expireAtDate = pref.getExpirationDate(carWork)
        ))
      }
      .onErrorReturn {
        saveReminder(newReminder(carWork, pref))
      }
      .map { reminder ->
//        cancelPreviousNotification(reminder)
//        scheduleNotification(reminder)
        reminder
      }
  }

  private fun cancelPreviousNotification(reminder: Reminder) {
    // TODO: find previous carWork/reminder and cancel corresponding notification
  }

  private fun scheduleNotification(reminder: Reminder) {
    val notificationId = reminder.id!!.toInt()
    val delivery = Calendar.getInstance().apply {
      // schedule at 8:00am
      reminder.expireAtDate.let { set(it.year, it.monthValue, it.dayOfMonth, 8, 0) }
    }
    NotificationService.scheduleNotification(context, reminder, notificationId, delivery)
  }

  private fun newReminder(carWork: CarWork, pref: Preference): Reminder {
    return Reminder(
        id = null,
        carId = carWork.carId,
        carWorkId = carWork.id!!,
        name = carWork.name,
        description = carWork.notes ?: "",
        type = ReminderType.UPCOMING_MAINTENANCE,
        currentMiles = carWork.miles,
        currentDate = carWork.date,
        expireAtMiles = carWork.miles + pref.miles,
        expireAtDate = pref.getExpirationDate(carWork)
    )
  }
}