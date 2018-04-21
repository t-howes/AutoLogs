package sample.thowes.autoservice.views.carList

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import io.reactivex.Single
import sample.thowes.autoservice.base.BaseViewModel
import sample.thowes.autoservice.extensions.applySchedulers
import sample.thowes.autoservice.models.Car

class CarsViewModel : BaseViewModel() {

  var state: MutableLiveData<CarResultsState> = MutableLiveData()
  private lateinit var carsLiveData: LiveData<List<Car>>
  private lateinit var carsObserver: Observer<List<Car>>

  enum class CarsStatus {
    IDLE,
    LOADING,
    ERROR,
    EMPTY,
    SUCCESS
  }

  fun getCars() {
    carsLiveData = carDb.getCars()
    carsObserver = Observer {
      Single.just(it)
          .applySchedulers()
          .flatMap {
            Single.just(it.sortedByDescending { it.year })
          }
          .doOnSubscribe { state.postValue(CarResultsState.loading()) }
          .subscribe({ cars ->
            if (cars == null || cars.isEmpty()) {
              state.postValue(CarResultsState.empty())
            } else {
              state.postValue(CarResultsState.success(cars))
            }
          }, { error ->
            state.postValue(CarResultsState.error(error))
          })
    }

    carsLiveData.observeForever(carsObserver)
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