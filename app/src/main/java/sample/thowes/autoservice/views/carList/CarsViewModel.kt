package sample.thowes.autoservice.views.carList

import android.arch.core.util.Function
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import io.reactivex.Observable
import sample.thowes.autoservice.base.BaseViewModel
import sample.thowes.autoservice.models.Car

class CarsViewModel : BaseViewModel() {

  var state: LiveData<CarDetailsState> = MutableLiveData()

  enum class CarsStatus {
    LOADING,
    ERROR,
    EMPTY,
    SUCCESS
  }

  fun getCars() {
    val carsLiveData = carDb.getCars()
    state = Transformations.map(carsLiveData, {
      if (it.isEmpty()) {
        CarDetailsState.empty()
      } else {
        CarDetailsState.success(cars = it.toList())
      }
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