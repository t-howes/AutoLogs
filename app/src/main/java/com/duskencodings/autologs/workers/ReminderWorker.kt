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
import com.duskencodings.autologs.utils.log.Logger
import com.duskencodings.autologs.utils.now
import io.reactivex.Single
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
    return Single.create<Result> { emitter ->
      findReminders()
          .subscribe({
            Logger.i("ReminderWorker", "Successfully scheduled Reminders.")
            emitter.onSuccess(Result.success())
          }, {
            Logger.d("ReminderWorker", "Failed to schedule Reminders.")
            emitter.onSuccess(Result.failure())
          }).also { addSub(it) }
    }.blockingGet()
  }

  private fun findReminders(): Single<List<Reminder>> {
    return remindersRepo.getAllReminders()
        .applySchedulers()
        .map { reminders ->
          reminders.filter { it.expireAtDate?.isBefore(now()) == false } // TODO: check miles
        }
        .doOnSuccess { reminders ->
          NotificationService.scheduleNotifications(applicationContext, reminders)
        }
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