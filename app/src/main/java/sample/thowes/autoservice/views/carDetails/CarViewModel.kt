package sample.thowes.autoservice.views.carDetails

import android.arch.lifecycle.MutableLiveData
import sample.thowes.autoservice.base.BaseViewModel
import sample.thowes.autoservice.extensions.applySchedulers
import sample.thowes.autoservice.models.Car

class CarViewModel : BaseViewModel() {

  var state: MutableLiveData<CarDetailsState> = MutableLiveData()

  enum class CarStatus {
    IDLE,
    LOADING,
    ERROR,
    SUCCESS
  }

  fun getCar(year: Int, make: String, model: String) {
    addSub(
        carDb.getCar(year, make, model)
            .applySchedulers()
            .doOnSubscribe { state.postValue(CarDetailsState.loading()) }
            .doAfterTerminate { state.postValue(CarDetailsState.idle()) }
            .subscribe({ car ->
              state.postValue(CarDetailsState.success(car))
            }, { error ->
              state.postValue(CarDetailsState.error(error))
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

      fun success(car: Car): CarDetailsState {
        return CarDetailsState(CarStatus.SUCCESS, car = car)
      }
    }
  }
}