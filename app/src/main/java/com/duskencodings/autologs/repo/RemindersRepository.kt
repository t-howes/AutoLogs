package com.duskencodings.autologs.repo

import android.content.Context
import androidx.lifecycle.LiveData
import com.duskencodings.autologs.base.BaseRepository
import com.duskencodings.autologs.database.RemindersDb
import com.duskencodings.autologs.models.CarWork
import com.duskencodings.autologs.models.Preference
import com.duskencodings.autologs.models.Reminder
import com.duskencodings.autologs.models.ReminderType
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.time.LocalDate

class RemindersRepository(
    context: Context,
    private val remindersDb: RemindersDb
) : BaseRepository(context) {

  fun getAllReminders(): Single<List<Reminder>> = remindersDb.getAllReminders()
  fun getReminders(carId: Long): Single<List<Reminder>> = remindersDb.getReminders(carId)
  fun getReminder(reminderId: Long): Single<Reminder> = remindersDb.getReminder(reminderId)
  private fun getSingleReminderFromCarWorkName(carWorkName: String, carId: Long): Single<Reminder> = remindersDb.getSingleByCarWorkName(carWorkName, carId)
  fun getReminderFromCarWorkName(carWorkName: String, carId: Long): Reminder? = remindersDb.getByCarWorkName(carWorkName, carId)
  private fun saveReminder(reminder: Reminder): Reminder = remindersDb.insertOrUpdate(reminder)
  fun getLiveUpcomingReminders(carId: Long): LiveData<List<Reminder>> = remindersDb.getLiveUpcomingReminders(carId)
  fun getNotificationReminders(): Single<List<Reminder>> = remindersDb.getNotificationReminders()

  fun saveOrCreateReminder(carWork: CarWork, pref: Preference): Single<Reminder> {
    return getSingleReminderFromCarWorkName(carWork.name, carWork.carId)
      .map { existingReminder ->
        // update the existing Reminder with new expiration fields
        saveReminder(existingReminder.copy(
            carWorkId = carWork.id!!,
            currentMiles = carWork.miles,
            currentDate = carWork.date,
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

  fun saveReminderDate(reminderId: Long, date: LocalDate): Completable {
    return Completable.fromCallable {
      remindersDb.saveReminderDate(reminderId, date)
    }.subscribeOn(Schedulers.io())
  }

  fun deleteReminder(reminderId: Long): Completable {
    return Completable.fromCallable {
      remindersDb.deleteReminder(reminderId)
    }.subscribeOn(Schedulers.io())
  }
}