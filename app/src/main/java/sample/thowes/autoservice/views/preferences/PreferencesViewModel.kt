package sample.thowes.autoservice.views.preferences

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import io.reactivex.Observable
import io.reactivex.Single
import sample.thowes.autoservice.base.BaseViewModel
import sample.thowes.autoservice.extensions.applySchedulers
import sample.thowes.autoservice.models.Car
import sample.thowes.autoservice.models.Preference
import sample.thowes.autoservice.models.Resource

class PreferencesViewModel : BaseViewModel() {
  var detailsState: MutableLiveData<Resource<List<Preference>>> = MutableLiveData()
  var submitState: MutableLiveData<Resource<Boolean>> = MutableLiveData()
  private lateinit var prefsLiveData: LiveData<List<Preference>>
  private lateinit var prefsObserver: Observer<List<Preference>>

  fun getCar(carId: Int? = null) {
    carId?.let {
      prefsLiveData = carDb.getLiveCar(carId)
      prefsObserver = Observer {
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

      prefsLiveData.observeForever(prefsObserver)
    } ?: { detailsState.value = Resource.idle() }.invoke()
  }

  fun updateCar(car: Car) {
    addSub(Observable.fromCallable {
      carDb.saveCar(car)
    }.applySchedulers()
        .doOnSubscribe { detailsState.value = Resource.loading() }
        .doAfterTerminate { detailsState.value = Resource.idle() }
        .subscribe({
          submitState.value = Resource.success(true)
        }, {
          submitState.value = Resource.error(it)
        }))
  }
}