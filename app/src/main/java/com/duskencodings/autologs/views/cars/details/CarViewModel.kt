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
import com.duskencodings.autologs.repo.CarRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class CarViewModel @Inject constructor(private val repo: CarRepository) : BaseViewModel() {

  var state: MutableLiveData<State> = MutableLiveData()
  private lateinit var carLiveData: LiveData<Car>
  private lateinit var carObserver: Observer<Car>

  fun getCar(carId: Int?) {
    carId?.let { id ->
      carLiveData = repo.getLiveCar(id)
      carObserver = Observer {
        Single.just(it)
            .applySchedulers()
            .doOnSubscribe { state.value = State.loadingDetails() }
            .doAfterTerminate {
              getReminders(id)
            }
            .flatMap {
              // TODO: get spent stats
            }
            .subscribe({ car ->
              car?.let {
                state.value = Resource.success(car)
              } ?: {
                state.value = Resource.error(NullPointerException("null car from Room"))
              }.invoke()
            }, { error ->
              state.value = Resource.error(error)
            })

      }

      carLiveData.observeForever(carObserver)
    } ?: { state.value = Resource.idle() }.invoke()
  }

  fun updateCar(car: Car) {
    addSub(
      repo.saveCar(car)
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

  fun getReminders(cardId: Int) {

  }

  enum class Status {
    LOADING_DETAILS,
    LOADING_REMINDERS,
    DETAILS,
    REMINDERS,
    ERROR_DETAILS,
    ERROR_REMINDERS
  }

  data class State(val status: Status,
                   val error: Throwable? = null,
                   val details: CarDetails? = null,
                   val reminders: List<Reminder>? = null) {

    companion object {
      fun loadingDetails() = State(Status.LOADING_DETAILS)
      fun loadingReminders() = State(Status.LOADING_REMINDERS)
      fun successDetails(details: CarDetails) = State(Status.DETAILS, details = details)
      fun successReminders(reminders: List<Reminder>) = State(Status.REMINDERS, reminders = reminders)
      fun errorDetails(error: Throwable) = State(Status.ERROR_DETAILS, error = error)
      fun errorReminders(error: Throwable) = State(Status.ERROR_REMINDERS, error = error)
    }
  }

  data class CarDetails(val totalSpent: Int, val modsSpent: Int, val maintenanceSpent: Int)
}
