package com.duskencodings.autologs.views.maintenance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.duskencodings.autologs.base.BaseViewModel
import com.duskencodings.autologs.utils.applySchedulers
import com.duskencodings.autologs.models.CarWork
import com.duskencodings.autologs.models.Resource
import com.duskencodings.autologs.repo.RemindersRepository
import com.duskencodings.autologs.repo.ServiceRepository
import com.duskencodings.autologs.utils.log.Logger
import io.reactivex.Observable
import javax.inject.Inject

class MaintenanceViewModel @Inject constructor(
    private val serviceRepo: ServiceRepository,
    private val remindersRepo: RemindersRepository
) : BaseViewModel() {

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
              // sort by mileage, group by date
              workList.sortedByDescending { work -> work.odometerReading }
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
        .doOnComplete {
          remindersRepo.addReminder(carWork).subscribe({
            Logger.i("Save Work -> Add Reminder", "Successfully saved car work and added reminder (if applicable)")
          }, {
            Logger.d("Save Work -> Add Reminder", "Failed to add a reminder for ${carWork.name}")
          }).also { addSub(it) }
        }
        .subscribe({
          submitState.value = Resource.success(carWork)
        }, {
          submitState.value = Resource.error(it)
        })
    )
  }
}