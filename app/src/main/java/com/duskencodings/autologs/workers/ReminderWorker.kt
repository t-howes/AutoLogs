package com.duskencodings.autologs.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.duskencodings.autologs.dagger.injector.Injector
import com.duskencodings.autologs.models.Car
import com.duskencodings.autologs.models.Reminder
import com.duskencodings.autologs.notifications.NotificationService
import com.duskencodings.autologs.repo.CarRepository
import com.duskencodings.autologs.repo.PreferencesRepository
import com.duskencodings.autologs.repo.RemindersRepository
import com.duskencodings.autologs.utils.applySchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class ReminderWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

  @Inject
  lateinit var carsRepo: CarRepository

  @Inject
  lateinit var remindersRepo: RemindersRepository

  @Inject
  lateinit var prefsRepo: PreferencesRepository

  private val disposables = CompositeDisposable()

  init {
    Injector.component.inject(this)
  }

  override fun onStopped() {
    super.onStopped()
    clearDisposables()
  }

  /**
   * Find all cars and search for reminders to publish for today.
   * These can either be because the Reminder's expiration date or x miles have past.
   */
  override fun doWork(): Result {
    carsRepo.getCars()
        .applySchedulers()
        .doOnSuccess { cars ->
          cars.forEach {
            val reminders = getRemindersForCar(it)
            NotificationService.publishNotifications(applicationContext, reminders)
          }
        }
        .subscribe({

        }, {
          // NO-OP
        }).also { addSub(it) }

    return Result.success()
  }

  private fun getRemindersForCar(car: Car): List<Reminder> {
    return listOf()
  }

  private fun addSub(disposable: Disposable?) {
    disposable?.let { disposables.add(it) }
  }

  private fun clearDisposables() {
    if (!disposables.isDisposed) {
      disposables.dispose()
    }
  }
}