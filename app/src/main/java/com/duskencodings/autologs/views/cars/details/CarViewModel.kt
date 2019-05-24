package com.duskencodings.autologs.views.cars.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.reactivex.Single
import com.duskencodings.autologs.base.BaseViewModel
import com.duskencodings.autologs.extensions.applySchedulers
import com.duskencodings.autologs.models.Car
import com.duskencodings.autologs.models.Reminder
import com.duskencodings.autologs.models.Resource
import com.duskencodings.autologs.models.SpendingBreakdown
import com.duskencodings.autologs.repo.CarRepository
import com.duskencodings.autologs.repo.RemindersRepository
import com.duskencodings.autologs.repo.ServiceRepository
import javax.inject.Inject

class CarViewModel @Inject constructor(private val carRepo: CarRepository,
                                       private val serviceRepo: ServiceRepository,
                                       private val remindersRepo: RemindersRepository) : BaseViewModel() {

  var state: MutableLiveData<State> = MutableLiveData()
  private lateinit var carLiveData: LiveData<Car>
  private lateinit var carObserver: Observer<Car>

  fun getCar(carId: Int?) {
    carId?.let { id ->
      carLiveData = carRepo.getLiveCar(id)
      carObserver = Observer { car ->
        Single.just(car)
            .applySchedulers()
            .doOnSubscribe { state.value = State.loadingDetails() }
            .doAfterSuccess { state.value = State.successCar(car) }
            .flatMap { serviceRepo.getSpendingDetails(carId).doOnError { state.value = State.errorDetails(it) } }
            .doAfterSuccess { state.value = State.successDetails(it) }
            .flatMap { remindersRepo.getUpcomingReminders(carId) }
            .doAfterSuccess { state.value = State.successReminders(it) }
            .subscribe({
              car?.let {
                state.value = Resource.success(car)
              } ?: {
                state.value = Resource.error(NullPointerException("null car from Room"))
              }.invoke()
            }, { error ->
              state.value = State.errorDetails(error)
            })

      }

      carLiveData.observeForever(carObserver)
    } ?: { state.value = Resource.idle() }.invoke()
  }

  fun updateCar(car: Car) {
    addSub(
      carRepo.saveCar(car)
        .applySchedulers()
        .doOnSubscribe { state.value = Resource.loading() }
        .doAfterTerminate { state.value = Resource.idle() }
        .subscribe({
          submitState.value = Resource.success(true)
        }, {
          submitState.value = Resource.error(it)
        })
    )
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
                   val details: SpendingBreakdown? = null,
                   val reminders: List<Reminder>? = null) {

    companion object {
      fun loadingDetails() = State(Status.LOADING_DETAILS)
      fun loadingReminders() = State(Status.LOADING_REMINDERS)
      fun successCar(car: Car) = State(Status.CAR, car = car)
      fun successDetails(details: SpendingBreakdown) = State(Status.DETAILS, details = details)
      fun successReminders(reminders: List<Reminder>) = State(Status.REMINDERS, reminders = reminders)
      fun errorDetails(error: Throwable) = State(Status.ERROR_DETAILS, error = error)
      fun errorReminders(error: Throwable) = State(Status.ERROR_REMINDERS, error = error)
    }
  }

}
