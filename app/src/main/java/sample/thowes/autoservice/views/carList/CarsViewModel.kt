package sample.thowes.autoservice.views.carList

import android.arch.lifecycle.LiveData
import io.reactivex.Observable
import sample.thowes.autoservice.base.BaseViewModel
import sample.thowes.autoservice.models.Car

class CarsViewModel : BaseViewModel() {

  private var carsLiveData: LiveData<Car>? = null

  enum class CarsState {
    LOADING,
    ERROR,
    SUCCESS
  }

  fun getObservableCars(): Observable<List<Car>> {
    return Observable.just(arrayListOf())
  }
}