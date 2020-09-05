package com.duskencodings.autologs.repo

import android.content.Context
import com.duskencodings.autologs.base.BaseRepository
import com.duskencodings.autologs.database.RemindersDb
import com.duskencodings.autologs.utils.applySchedulers
import com.duskencodings.autologs.models.CarWork
import com.duskencodings.autologs.models.Preference
import com.duskencodings.autologs.models.Reminder
import com.duskencodings.autologs.models.ReminderType
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class RemindersRepository(
    context: Context,
    private val remindersDb: RemindersDb,
    private val preferencesRepo: PreferencesRepository
) : BaseRepository(context) {

  fun getReminders(carId: Int): Single<List<Reminder>> = remindersDb.getReminders(carId)
  private fun getReminder(carId: Int, jobName: String): Single<Reminder> = remindersDb.getReminderForCarByJobName(carId, jobName)

  fun addReminder(carWork: CarWork): Single<Pair<Preference, Reminder>> {
    return preferencesRepo.getPreferenceByCarAndName(carWork.carId, carWork.name)
        .applySchedulers(observeOn = Schedulers.io())
        .map { pref ->
          val reminder = getReminder(carWork.carId, carWork.name)
              // if we don't have a saved reminder or fail to fetch, create a new one
              .onErrorReturn { newReminder(carWork, pref) }
              // save updated reminder
              .doOnSuccess { remindersDb.insertOrUpdate(it) }

          Pair(pref, reminder)
        }
  }

  private fun newReminder(carWork: CarWork, pref: Preference): Reminder {
    return Reminder(
        id = null,
        carId = carWork.carId,
        name = carWork.name,
        description = "Empty for now",
        type = ReminderType.UPCOMING_MAINTENANCE,
        currentMiles = carWork.odometerReading,
        currentDate = carWork.date,
        expireMiles = carWork.odometerReading + pref.miles,
        expireDate = carWork.date.plusMonths(pref.months.toLong())
    )
  }
}