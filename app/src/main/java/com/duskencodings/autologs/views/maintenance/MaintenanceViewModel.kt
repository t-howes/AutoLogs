package com.duskencodings.autologs.views.maintenance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.duskencodings.autologs.base.BaseViewModel
import com.duskencodings.autologs.extensions.applySchedulers
import com.duskencodings.autologs.models.CarWork
import com.duskencodings.autologs.models.Resource
import com.duskencodings.autologs.repo.RemindersRepository
import com.duskencodings.autologs.repo.ServiceRepository
import io.reactivex.Observable
import javax.inject.Inject

class MaintenanceViewModel @Inject constructor(private val serviceRepo: ServiceRepository, private val remindersRepo: RemindersRepository) : BaseViewModel() {

  val detailsState: MutableLiveData<Resource<CarWork>> = MutableLiveData()
  val listState: MutableLiveData<Resource<List<CarWork>>> = MutableLiveData()
  val submitState: MutableLiveData<Resource<CarWork>> = MutableLiveData()

  private lateinit var maintenanceLiveData: LiveData<List<CarWork>>
  private lateinit var maintenanceObserver: Observer<List<CarWork>>
  var carId: Int? = null
  var maintenanceId: Int? = null

  fun getLiveCarWorkRecords() {
    carId?.let {
      maintenanceLiveData = serviceRepo.getLiveCarWorkList(it)
      maintenanceObserver = Observer {
        Observable.just(it)
            .applySchedulers()
            .doOnSubscribe { listState.value = Resource.loading() }
            .doAfterTerminate { listState.value = Resource.idle() }
            .map { workList ->
              // group by date (sorted) then sort by odometer/miles
              workList.sortedByDescending { work -> work.date }.groupBy { work -> work.date }.apply {
//                forEach { date, list ->
//                  list.sortedByDescending { work -> work.odometerReading }
//                }
              }.flatMap { entry -> entry.value }
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

  fun getMaintenance() {
    maintenanceId?.let {
      addSub(serviceRepo.getCarWork(it)
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

  fun saveWork(carWork: CarWork) {
    addSub(
      serviceRepo.saveCarWork(carWork)
        .applySchedulers()
        .doOnSubscribe { submitState.value = Resource.loading() }
        .doAfterTerminate { submitState.value = Resource.idle() }
        .doOnSubscribe {
          remindersRepo.addReminder(carWork)
        }
        .subscribe({
          submitState.value = Resource.success(carWork)
        }, {
          submitState.value = Resource.error(it)
        })
    )

  }
}