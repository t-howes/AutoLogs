package com.duskencodings.autologs.views.cars.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.reactivex.Single
import com.duskencodings.autologs.base.BaseViewModel
import com.duskencodings.autologs.extensions.applySchedulers
import com.duskencodings.autologs.models.Car
import com.duskencodings.autologs.models.Resource
import com.duskencodings.autologs.repo.CarRepository
import javax.inject.Inject

class CarViewModel @Inject constructor(private val repo: CarRepository) : BaseViewModel() {

  var detailsState: MutableLiveData<Resource<Car>> = MutableLiveData()
  var submitState: MutableLiveData<Resource<Boolean>> = MutableLiveData()
  private lateinit var carLiveData: LiveData<Car>
  private lateinit var carObserver: Observer<Car>

  fun getCar(carId: Int?) {
    carId?.let {
      carLiveData = repo.getLiveCar(carId)
      carObserver = Observer {
        Single.just(it)
            .applySchedulers()
            .doOnSubscribe { detailsState.value = Resource.loading() }
            .doAfterTerminate { detailsState.value = Resource.idle() }
            .subscribe({ car ->
              car?.let {
                detailsState.value = Resource.success(car)
              } ?: {
                detailsState.value = Resource.error(NullPointerException("null car from Room"))
              }.invoke()
            }, { error ->
              detailsState.value = Resource.error(error)
            })

      }

      carLiveData.observeForever(carObserver)
    } ?: { detailsState.value = Resource.idle() }.invoke()
  }

  fun updateCar(car: Car) {
    addSub(
      repo.saveCar(car)
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