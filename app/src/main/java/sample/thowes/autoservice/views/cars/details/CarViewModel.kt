package sample.thowes.autoservice.views.cars.details

import android.arch.lifecycle.MutableLiveData
import io.reactivex.Observable
import sample.thowes.autoservice.base.BaseViewModel
import sample.thowes.autoservice.extensions.applySchedulers
import sample.thowes.autoservice.models.Car

class CarViewModel : BaseViewModel() {

  var state: MutableLiveData<CarDetailsState> = MutableLiveData()

  enum class CarStatus {
    IDLE,
    LOADING,
    ERROR,
    CAR_RETRIEVED,
    SUBMIT
  }

  fun getCar(carId: Int? = null) {
    carId?.let {
      addSub(carDb.getCar(carId)
          .applySchedulers()
          .doOnSubscribe { state.postValue(CarDetailsState.loading()) }
          .subscribe({ car ->
            state.postValue(CarDetailsState.carRetrieved(car))
          }, { error ->
            state.postValue(CarDetailsState.error(error))
          }))
    } ?: state.postValue(CarDetailsState.idle())
  }

  fun updateCar(car: Car) {
    addSub(Observable.fromCallable {
        carDb.saveCar(car)
      }.applySchedulers()
        .doOnSubscribe { state.postValue(CarDetailsState.loading()) }
        .subscribe({
          state.postValue(CarDetailsState.submit())
        }, {
          state.postValue(CarDetailsState.error(it))
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