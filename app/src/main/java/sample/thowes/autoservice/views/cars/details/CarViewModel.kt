package sample.thowes.autoservice.views.cars.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.reactivex.Observable
import io.reactivex.Single
import sample.thowes.autoservice.base.BaseViewModel
import sample.thowes.autoservice.extensions.applySchedulers
import sample.thowes.autoservice.models.Car
import sample.thowes.autoservice.models.Resource

class CarViewModel : BaseViewModel() {

  var detailsState: MutableLiveData<Resource<Car>> = MutableLiveData()
  var submitState: MutableLiveData<Resource<Boolean>> = MutableLiveData()
  private lateinit var carLiveData: LiveData<Car>
  private lateinit var carObserver: Observer<Car>

  fun getCar(carId: Int? = null) {
    carId?.let {
      carLiveData = carDb.getLiveCar(carId)
      carObserver = Observer {
        Single.just(it)
            .applySchedulers()
            .doOnSubscribe { detailsState.value = Resource.loading() }
            .doAfterTerminate { detailsState.value = Resource.idle() }
            .subscribe({ car ->
              car?.let {
                detailsState.value = Resource.success(car)
              } ?: { detailsState.value = Resource.error(NullPointerException("null car from Room")) }.invoke()
            }, { error ->
              detailsState.value = Resource.error(error)
            })

      }

      carLiveData.observeForever(carObserver)
    } ?: { detailsState.value = Resource.idle() }.invoke()
  }

  fun updateCar(car: Car) {
    addSub(
      Single.fromCallable {
        carDb.insertOrUpdate(car)
      }
      .applySchedulers()
      .doOnSubscribe { detailsState.value = Resource.loading() }
      .doAfterTerminate { detailsState.value = Resource.idle() }
      .subscribe({
        submitState.value = Resource.success(true)
      }, {
        submitState.value = Resource.error(it)
      })
    )
  }
}