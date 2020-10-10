package com.duskencodings.autologs.views.cars.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.reactivex.Single
import com.duskencodings.autologs.base.BaseViewModel
import com.duskencodings.autologs.utils.applySchedulers
import com.duskencodings.autologs.models.*
import com.duskencodings.autologs.repo.CarRepository
import com.duskencodings.autologs.repo.RemindersRepository
import com.duskencodings.autologs.repo.ServiceRepository
import com.duskencodings.autologs.utils.log.Logger
import com.duskencodings.autologs.views.maintenance.upcoming.ReminderAdapter
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class CarDetailsViewModel @Inject constructor(private val carRepo: CarRepository,
                                              private val serviceRepo: ServiceRepository,
                                              private val remindersRepo: RemindersRepository) : BaseViewModel() {

  var carId: Long? = null // will have a valid ID passed in args
  var state: PublishSubject<State> = PublishSubject.create() // trying this as an alt to LiveData
  lateinit var reminderAdapter: ReminderAdapter
  private lateinit var carLiveData: LiveData<Car>
  private lateinit var carObserver: Observer<Car>
  private lateinit var maintenanceLiveData: LiveData<List<CarWork>>
  private lateinit var maintenanceObserver: Observer<List<CarWork>>
  private lateinit var remindersLiveData: LiveData<List<Reminder>>
  private lateinit var remindersObserver: Observer<List<Reminder>>

  fun loadScreen(carId: Long) {
    this.carId = carId
    state.onNext(State.initUi())
    observeReminders(carId)

    carLiveData = carRepo.getLiveCar(carId)
    carObserver = Observer { car ->
      car?.let {
        Single.just(car)
            .applySchedulers()
            .doOnSubscribe { state.onNext(State.loadingDetails()) }
            .doAfterSuccess {
              // if we fail to fetch car work, then the lateinit live data and observer will crash.
              observeCarWork(it.id!!)
            }
            .subscribe({
              state.onNext(State.successCar(car))
            }, { error ->
              state.onNext(State.error(error))
            })
            .also { addSub(it) }
      } ?: run {
        state.onNext(State.error(NullPointerException("null car from Room")))
      }
    }

    carLiveData.observeForever(carObserver)
  }

  private fun observeCarWork(carId: Long) {
    maintenanceLiveData = serviceRepo.getLiveCarWorkList(carId)
    maintenanceObserver = Observer {
      Observable.just(it)
          .applySchedulers()
          .doOnNext { state.onNext(State.loadingDetails()) }
          .map { work ->
            val maintenanceJobs = work.filter { job -> job.type == CarWork.Type.MAINTENANCE }
            val maintenanceCost = maintenanceJobs.sumByDouble { job -> job.cost ?: 0.0 }
            val modsCost = work.minus(maintenanceJobs).sumByDouble { job -> job.cost ?: 0.0 }
            SpendingBreakdown(maintenanceCost, modsCost)
          }
          .subscribe({ spending ->
            state.onNext(State.successSpending(spending))
          }, { error ->
            state.onNext(State.error(error))
          }).also { addSub(it) }
    }

    maintenanceLiveData.observeForever(maintenanceObserver)
  }

  private fun observeReminders(carId: Long) {
    remindersLiveData = remindersRepo.getLiveUpcomingReminders(carId)
    remindersObserver = Observer {
      Observable.just(it)
        .applySchedulers()
        .doOnNext { state.onNext(State.loadingReminders()) }
        .subscribe({ reminders ->
          state.onNext(State.successReminders(reminders))
        }, { error ->
          state.onNext(State.error(error))
        }).also { addSub(it) }
    }

    remindersLiveData.observeForever(remindersObserver)
  }

  fun deleteCar() {
    carRepo.deleteCar(carId!!)
      .applySchedulers()
      .doOnSubscribe { state.onNext(State.loadingDetails()) }
      .subscribe({
        carLiveData.removeObserver(carObserver)
        state.onNext(State.successDelete())
      }, {
        Logger.e("CAR DELETE", "Failed to delete car", it)
        state.onNext(State.error(RuntimeException("Unable to delete car at this time")))
      }).also { addSub(it) }
  }

  enum class Status {
    INIT_UI,
    LOADING_DETAILS,
    LOADING_REMINDERS,
    CAR,
    CAR_DELETED,
    SPENDING,
    REMINDERS,
    ERROR_DETAILS,
    ERROR_REMINDERS
  }

  data class State(val status: Status,
                   val error: Throwable? = null,
                   val car: Car? = null,
                   val spendingBreakdown: SpendingBreakdown? = null,
                   val reminders: List<Reminder>? = null) {

    companion object {
      fun initUi() = State(Status.INIT_UI)
      fun loadingDetails() = State(Status.LOADING_DETAILS)
      fun loadingReminders() = State(Status.LOADING_REMINDERS)
      fun successCar(car: Car) = State(Status.CAR, car = car)
      fun successDelete() = State(Status.CAR_DELETED)
      fun successSpending(spending: SpendingBreakdown) = State(Status.SPENDING, spendingBreakdown = spending)
      fun successReminders(reminders: List<Reminder>) = State(Status.REMINDERS, reminders = reminders)
      fun error(error: Throwable) = State(Status.ERROR_DETAILS, error = error)
      fun errorReminders(error: Throwable) = State(Status.ERROR_REMINDERS, error = error)
    }
  }

}
