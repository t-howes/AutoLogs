package com.duskencodings.autologs.views.cars.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.duskencodings.autologs.base.BaseViewModel
import com.duskencodings.autologs.utils.applySchedulers
import com.duskencodings.autologs.models.Car
import com.duskencodings.autologs.models.Reminder
import com.duskencodings.autologs.models.SpendingBreakdown
import com.duskencodings.autologs.repo.CarRepository
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class AddCarViewModel @Inject constructor(private val carRepo: CarRepository) : BaseViewModel() {

    var carId: Long? = null
    var state: PublishSubject<State> = PublishSubject.create()
    private lateinit var carLiveData: LiveData<Car>
    private lateinit var carObserver: Observer<Car>

    fun loadScreen() {
      carId?.let { id ->
        carLiveData = carRepo.getLiveCar(id)
        carObserver = Observer { car ->
          car?.let {
            Single.just(car)
                .applySchedulers()
                .doOnSubscribe { state.onNext(State.loading()) }
                .doAfterTerminate { state.onNext(State.idle()) }
                .subscribe({
                  state.onNext(State.successCar(it))
                }, { error ->
                  state.onNext(State.error(error))
                })
          } ?: run {
            state.onNext(State.idle())
          }
        }

        carLiveData.observeForever(carObserver)
      } ?: run {
        state.onNext(State.idle())
      }
    }

    fun updateCar(car: Car) {
      addSub(
          carRepo.saveCar(car)
              .applySchedulers()
              .doOnSubscribe { state.onNext(State.loading()) }
              .doAfterTerminate { state.onNext(State.idle()) }
              .subscribe({
                state.onNext(State.saved())
              }, {
                state.onNext(State.error(it))
              })
      )
    }

    enum class Status {
      LOADING,
      IDLE,
      CAR_DETAILS,
      SAVED,
      ERROR
    }

    data class State(val status: Status,
                     val error: Throwable? = null,
                     val car: Car? = null,
                     val spendingBreakdown: SpendingBreakdown? = null,
                     val reminders: List<Reminder>? = null) {

      companion object {
        fun loading() = State(Status.LOADING)
        fun idle() = State(Status.IDLE)
        fun successCar(car: Car) = State(Status.CAR_DETAILS, car = car)
        fun saved() = State(Status.SAVED)
        fun error(error: Throwable) = State(Status.ERROR, error = error)
      }
    }

  }