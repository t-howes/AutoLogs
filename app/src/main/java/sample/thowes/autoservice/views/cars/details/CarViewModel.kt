package sample.thowes.autoservice.views.cars.details

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import io.reactivex.Observable
import io.reactivex.Single
import sample.thowes.autoservice.base.BaseViewModel
import sample.thowes.autoservice.extensions.applySchedulers
import sample.thowes.autoservice.models.Car

class CarViewModel : BaseViewModel() {

  var state: MutableLiveData<CarDetailsState> = MutableLiveData()
  private lateinit var carLiveData: LiveData<Car>
  private lateinit var carObserver: Observer<Car>

  enum class CarStatus {
    IDLE,
    LOADING,
    ERROR,
    CAR_RETRIEVED,
    SUBMIT
  }

  fun getCar(carId: Int? = null) {
    carId?.let {
      carLiveData = carDb.getLiveCar(carId)
      carObserver = Observer {
        Single.just(it)
            .applySchedulers()
            .doOnSubscribe { state.value = CarDetailsState.loading() }
            .subscribe({ car ->
              car?.let {
                state.value = CarDetailsState.carRetrieved(car)
              } ?: { state.value = CarDetailsState.error(NullPointerException("null car from Room")) }.invoke()
            }, { error ->
              state.value = CarDetailsState.error(error)
            })

      }

      carLiveData.observeForever(carObserver)
    } ?: state.postValue(CarDetailsState.idle())
  }

  fun updateCar(car: Car) {
    addSub(Observable.fromCallable {
        carDb.saveCar(car)
      }.applySchedulers()
        .doOnSubscribe { state.value = CarDetailsState.loading() }
        .subscribe({
          state.value = CarDetailsState.submit()
        }, {
          state.value = CarDetailsState.error(it)
        }))
  }

  class CarDetailsState(val status: CarStatus,
                        val error: Throwable? = null,
                        val car: Car? = null) {

    companion object {
      fun idle(): CarDetailsState {
        return CarDetailsState(CarStatus.IDLE)
      }

      fun loading(): CarDetailsState {
        return CarDetailsState(CarStatus.LOADING)
      }

      fun error(error: Throwable): CarDetailsState {
        return CarDetailsState(CarStatus.ERROR, error)
      }

      fun carRetrieved(car: Car): CarDetailsState {
        return CarDetailsState(CarStatus.CAR_RETRIEVED, car = car)
      }

      fun submit(): CarDetailsState {
        return CarDetailsState(CarStatus.SUBMIT)
      }
    }
  }
}