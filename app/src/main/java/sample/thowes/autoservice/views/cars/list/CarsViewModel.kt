package sample.thowes.autoservice.views.cars.list

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import sample.thowes.autoservice.base.BaseViewModel
import sample.thowes.autoservice.extensions.applySchedulers
import sample.thowes.autoservice.log.Logger
import sample.thowes.autoservice.models.Car
import sample.thowes.autoservice.models.Resource

class CarsViewModel : BaseViewModel() {

  var state: MutableLiveData<Resource<List<Car>>> = MutableLiveData()
  private lateinit var carsLiveData: LiveData<List<Car>>
  private lateinit var carsObserver: Observer<List<Car>>

  fun getCars() {
    carsLiveData = carDb.getCars()
    carsObserver = Observer {
      Single.just(it)
          .applySchedulers()
          .flatMap {
            Single.just(it.sortedByDescending { it.year })
          }
          .doOnSubscribe { state.value = Resource.loading() }
          .doAfterTerminate { state.value = Resource.idle() }
          .subscribe({ cars ->
            state.value = Resource.success(cars)
          }, { error ->
            state.value = Resource.error(error)
          })
    }

    carsLiveData.observeForever(carsObserver)
  }

  fun deleteCar(car: Car) {
    addSub(Observable.fromCallable {
        carDb.deleteCar(car)
      }.subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .doOnSubscribe { state.value = Resource.loading() }
      .doAfterTerminate { state.value = Resource.idle() }
      .subscribe({
        // do nothing here, car will disappear
      }, {
        Logger.e("CAR DELETE", "Failed to delete car: ${it.localizedMessage}")
        state.value = Resource.error(RuntimeException("Unable to delete car at this time"))
      })
    )
  }
}