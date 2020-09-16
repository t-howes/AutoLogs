package com.duskencodings.autologs.repo

import android.content.Context
import androidx.lifecycle.LiveData
import com.duskencodings.autologs.base.BaseRepository
import com.duskencodings.autologs.database.RemindersDb
import com.duskencodings.autologs.models.CarWork
import com.duskencodings.autologs.models.Preference
import com.duskencodings.autologs.models.Reminder
import com.duskencodings.autologs.models.ReminderType
import com.duskencodings.autologs.utils.applySchedulers
import com.duskencodings.autologs.utils.now
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class RemindersRepository(
    context: Context,
    private val remindersDb: RemindersDb
) : BaseRepository(context) {

  fun getAllReminders(): Single<List<Reminder>> = remindersDb.getAllReminders()
  fun getReminders(carId: Long): Single<List<Reminder>> = remindersDb.getReminders(carId)
  fun getLiveReminders(carId: Long): LiveData<List<Reminder>> = remindersDb.getLiveReminders(carId)
  private fun getReminder(carId: Long, jobName: String): Single<Reminder> = remindersDb.getReminderForCarByJobName(carId, jobName)
  private fun saveReminder(reminder: Reminder): Reminder = remindersDb.insertOrUpdate(reminder)

  fun addReminder(carWork: CarWork, pref: Preference): Single<Reminder> {
    return getReminder(carWork.carId, carWork.name)
      .map { existingReminder ->
        // update the existing Reminder with new expiration fields
        saveReminder(existingReminder.copy(
            expireAtMiles = carWork.odometerReading + pref.miles,
            expireAtDate = pref.months?.toLong()?.let { monthsAway ->
              (carWork.date ?: now()).plusMonths(monthsAway)
            }
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
        name = carWork.name,
        description = carWork.notes ?: "",
        type = ReminderType.UPCOMING_MAINTENANCE,
        currentMiles = carWork.odometerReading,
        currentDate = carWork.date,
        expireAtMiles = carWork.odometerReading + pref.miles,
        expireAtDate = pref.months?.toLong()?.let { carWork.date.plusMonths(it) }
    )
  }

  fun getUpcomingReminders(carId: Int): Single<List<Reminder>> = remindersDb.getUpcomingReminders(carId)
}