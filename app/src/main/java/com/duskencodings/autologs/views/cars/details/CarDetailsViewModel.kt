package com.duskencodings.autologs.views.cars.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.reactivex.Single
import com.duskencodings.autologs.base.BaseViewModel
import com.duskencodings.autologs.extensions.applySchedulers
import com.duskencodings.autologs.models.Car
import com.duskencodings.autologs.models.Reminder
import com.duskencodings.autologs.models.SpendingBreakdown
import com.duskencodings.autologs.repo.CarRepository
import com.duskencodings.autologs.repo.RemindersRepository
import com.duskencodings.autologs.repo.ServiceRepository
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class CarDetailsViewModel @Inject constructor(private val carRepo: CarRepository,
                                              private val serviceRepo: ServiceRepository,
                                              private val remindersRepo: RemindersRepository) : BaseViewModel() {

  var state: PublishSubject<State> = PublishSubject.create()
  private lateinit var carLiveData: LiveData<Car>
  private lateinit var carObserver: Observer<Car>

  fun loadScreen(carId: Int?) {
    carId?.let { id ->
      carLiveData = carRepo.getLiveCar(id)
      carObserver = Observer { car ->
        car?.let {
          Single.just(car)
              .applySchedulers()
              .doOnSubscribe { state.onNext(State.loadingDetails()) }
              .doAfterSuccess { state.onNext(State.successCar(car)) }
              .flatMap {
                serviceRepo.getSpendingDetails(carId).onErrorReturn {
                  // return empty obj on error to keep the stream going.
                  SpendingBreakdown(0.0, 0.0, 0.0)
                }
              }
              .doAfterSuccess {
                state.onNext(State.successDetails(it))
                state.onNext(State.loadingReminders())
              }
              .flatMap { remindersRepo.getUpcomingReminders(carId).onErrorReturn { listOf() } }
              .doAfterSuccess { state.onNext(State.successReminders(it)) }
              .subscribe({
                // posted all success or default states
              }, { error ->
                state.onNext(State.errorDetails(error))
              })
        } ?: run {
          state.onNext(State.errorDetails(NullPointerException("null car from Room")))
        }
      }

      carLiveData.observeForever(carObserver)
    } ?: run {
      state.onNext(State.errorDetails(NullPointerException("null car ID")))
    }
  }

  enum class Status {
    LOADING_DETAILS,
    LOADING_REMINDERS,
    CAR,
    DETAILS,
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
      fun loadingDetails() = State(Status.LOADING_DETAILS)
      fun loadingReminders() = State(Status.LOADING_REMINDERS)
      fun successCar(car: Car) = State(Status.CAR, car = car)
      fun successDetails(details: SpendingBreakdown) = State(Status.DETAILS, spendingBreakdown = details)
      fun successReminders(reminders: List<Reminder>) = State(Status.REMINDERS, reminders = reminders)
      fun errorDetails(error: Throwable) = State(Status.ERROR_DETAILS, error = error)
      fun errorReminders(error: Throwable) = State(Status.ERROR_REMINDERS, error = error)
    }
  }

}
