package com.duskencodings.autologs.repo

import android.content.Context
import com.duskencodings.autologs.base.BaseRepository
import com.duskencodings.autologs.database.RemindersDb
import com.duskencodings.autologs.extensions.applySchedulers
import com.duskencodings.autologs.models.CarWork
import com.duskencodings.autologs.models.Reminder
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class RemindersRepository(context: Context, private val db: RemindersDb, private val preferencesRepo: PreferencesRepository) : BaseRepository(context) {

  fun getUpcomingReminders(carId: Int): Single<List<Reminder>> {
    //TODO
    return Single.just(listOf())
  }

  fun addReminder(carWork: CarWork) {
    preferencesRepo.getPreferenceByCarAndName(carWork.id!!, carWork.name)
        .applySchedulers(observeOn = Schedulers.io())
        .doOnSuccess {

        }
        .doOnError {

        }
        .subscribe({
          // saved a new pref or updated existing
        }, {
          // sad day
        }).also { addDisposable(it) }
  }

}