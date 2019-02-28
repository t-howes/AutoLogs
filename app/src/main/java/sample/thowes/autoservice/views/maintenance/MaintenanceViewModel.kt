package sample.thowes.autoservice.views.maintenance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.reactivex.Observable
import io.reactivex.Single
import sample.thowes.autoservice.base.BaseViewModel
import sample.thowes.autoservice.extensions.applySchedulers
import sample.thowes.autoservice.models.CarWork
import sample.thowes.autoservice.models.Resource

class MaintenanceViewModel : BaseViewModel() {

  val detailsState: MutableLiveData<Resource<CarWork>> = MutableLiveData()
  val listState: MutableLiveData<Resource<List<CarWork>>> = MutableLiveData()
  val submitState: MutableLiveData<Resource<CarWork>> = MutableLiveData()

  private lateinit var maintenanceLiveData: LiveData<List<CarWork>>
  private lateinit var maintenanceObserver: Observer<List<CarWork>>

  fun getLiveCarWorkRecords(carId: Int? = null) {
    carId?.let {
      maintenanceLiveData = carWorkDb.getLiveCarWorkList(carId)
      maintenanceObserver = Observer {
        Single.just(it)
            .applySchedulers()
            .doOnSubscribe { listState.value = Resource.loading() }
            .doAfterTerminate { listState.value = Resource.idle() }
            .flatMap { Single.just(it.sortedByDescending { it.date }
                                     .sortedByDescending { it.odometerReading }) }
            .subscribe({ maintenance ->
              listState.value = Resource.success(maintenance)
            }, { error ->
              listState.value = Resource.error(error)
            })
      }

      maintenanceLiveData.observeForever(maintenanceObserver)
    } ?: {
      listState.value = Resource.idle()
      listState.value = Resource.error(NullPointerException("null carId when getting car work records"))
    }.invoke()
  }

  fun getMaintenance(id: Int? = null) {
    id?.let {
      addSub(carWorkDb.getCarWork(id)
          .applySchedulers()
          .doOnSubscribe { detailsState.value = Resource.loading() }
          .doAfterTerminate { detailsState.value = Resource.idle() }
          .subscribe({ maintenance ->
            detailsState.value = Resource.success(maintenance)
          }, { error ->
            detailsState.value = Resource.error(error)
          }))
    } ?: { detailsState.value = Resource.idle() }.invoke()
  }

  fun updateCar(carWork: CarWork) {
    addSub(
      Observable.fromCallable {
        carWorkDb.addCarWork(carWork)
      }
      .applySchedulers()
      .doOnSubscribe { submitState.value = Resource.loading() }
      .doAfterTerminate { submitState.value = Resource.idle() }
      .subscribe({
        submitState.value = Resource.success(carWork)
      }, {
        submitState.value = Resource.error(it)
      })
    )

  }
}