package sample.thowes.autoservice.views.carList

import android.arch.core.util.Function
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import sample.thowes.autoservice.base.BaseViewModel
import sample.thowes.autoservice.models.Car

class CarsViewModel : BaseViewModel() {

  var state: MutableLiveData<CarDetailsState> = MutableLiveData()

  enum class CarsStatus {
    LOADING,
    ERROR,
    EMPTY,
    SUCCESS
  }

  fun getCars() {
    carDb.getCars()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSubscribe { state.postValue(CarDetailsState.loading()) }
        .subscribe({ cars ->
          if (cars.isEmpty()) {
            state.postValue(CarDetailsState.empty())
          } else {
            state.postValue(CarDetailsState.success(cars))
          }
        }, { error ->
          state.postValue(CarDetailsState.error(error))
        })

  }

  data class CarDetailsState(val status: CarsStatus,
                             val error: Throwable? = null,
                             val cars: List<Car>? = null) {

    companion object {
      fun loading(): CarDetailsState {
        return CarDetailsState(CarsStatus.LOADING)
      }

      fun error(error: Throwable): CarDetailsState {
        return CarDetailsState(CarsStatus.ERROR, error)
      }

      fun empty(): CarDetailsState {
        return CarDetailsState(CarsStatus.EMPTY)
      }

      fun success(cars: List<Car>): CarDetailsState {
        return CarDetailsState(CarsStatus.SUCCESS, cars = cars)
      }
    }
  }
}