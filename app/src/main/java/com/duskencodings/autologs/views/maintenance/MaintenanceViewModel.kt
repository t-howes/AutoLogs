package com.duskencodings.autologs.views.maintenance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.reactivex.Single
import com.duskencodings.autologs.base.BaseViewModel
import com.duskencodings.autologs.extensions.applySchedulers
import com.duskencodings.autologs.models.CarWork
import com.duskencodings.autologs.models.Resource
import com.duskencodings.autologs.repo.ServiceRepository
import javax.inject.Inject

class MaintenanceViewModel @Inject constructor(private val repo: ServiceRepository) : BaseViewModel() {

  val detailsState: MutableLiveData<Resource<CarWork>> = MutableLiveData()
  val listState: MutableLiveData<Resource<List<CarWork>>> = MutableLiveData()
  val submitState: MutableLiveData<Resource<CarWork>> = MutableLiveData()

  private lateinit var maintenanceLiveData: LiveData<List<CarWork>>
  private lateinit var maintenanceObserver: Observer<List<CarWork>>

  fun getLiveCarWorkRecords(carId: Int? = null) {
    carId?.let {
      maintenanceLiveData = repo.getLiveCarWorkList(carId)
      maintenanceObserver = Observer {
        Single.just(it)
            .applySchedulers()
            .doOnSubscribe { listState.value = Resource.loading() }
            .doAfterTerminate { listState.value = Resource.idle() }
            .flatMap { workList ->
              Single.just(workList.sortedByDescending { work -> work.date }
                                  .sortedByDescending { work -> work.odometerReading })
            }
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
      addSub(repo.getCarWork(id)
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
      repo.saveCarWork(carWork)
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