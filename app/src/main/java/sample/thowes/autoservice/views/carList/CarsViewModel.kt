package sample.thowes.autoservice.views.carList

import android.arch.lifecycle.MutableLiveData
import sample.thowes.autoservice.base.BaseViewModel
import sample.thowes.autoservice.extensions.applySchedulers
import sample.thowes.autoservice.models.Car

class CarsViewModel : BaseViewModel() {

  var state: MutableLiveData<CarResultsState> = MutableLiveData()

  enum class CarsStatus {
    IDLE,
    LOADING,
    ERROR,
    EMPTY,
    SUCCESS
  }

  fun getCars() {
    addSub(
      carDb.getCars()
          .applySchedulers()
          .doOnSubscribe { state.postValue(CarResultsState.loading()) }
          .doAfterTerminate { state.postValue(CarResultsState.idle()) }
          .subscribe({ cars ->
            if (cars.isEmpty()) {
              state.postValue(CarResultsState.empty())
            } else {
              state.postValue(CarResultsState.success(cars))
            }
          }, { error ->
            state.postValue(CarResultsState.error(error))
          }))
  }

  class CarResultsState(val status: CarsStatus,
                        val error: Throwable? = null,
                        val cars: List<Car>? = null) {

    companion object {
      fun idle(): CarResultsState {
        return CarResultsState(CarsStatus.IDLE)
      }

      fun loading(): CarResultsState {
        return CarResultsState(CarsStatus.LOADING)
      }

      fun error(error: Throwable): CarResultsState {
        return CarResultsState(CarsStatus.ERROR, error)
      }

      fun empty(): CarResultsState {
        return CarResultsState(CarsStatus.EMPTY)
      }

      fun success(cars: List<Car>): CarResultsState {
        return CarResultsState(CarsStatus.SUCCESS, cars = cars)
      }
    }
  }
}