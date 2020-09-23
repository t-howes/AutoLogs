package com.duskencodings.autologs.repo

import android.content.Context
import androidx.lifecycle.LiveData
import com.duskencodings.autologs.base.BaseRepository
import com.duskencodings.autologs.database.RemindersDb
import com.duskencodings.autologs.models.CarWork
import com.duskencodings.autologs.models.Preference
import com.duskencodings.autologs.models.Reminder
import com.duskencodings.autologs.models.ReminderType
import io.reactivex.Single

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

  fun addReminder(carWork: CarWork, pref: Preference): Single<Reminder> {
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