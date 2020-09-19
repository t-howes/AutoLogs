package com.duskencodings.autologs.views.maintenance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.duskencodings.autologs.base.BaseViewModel
import com.duskencodings.autologs.models.*
import com.duskencodings.autologs.utils.applySchedulers
import com.duskencodings.autologs.repo.PreferencesRepository
import com.duskencodings.autologs.repo.RemindersRepository
import com.duskencodings.autologs.repo.ServiceRepository
import com.duskencodings.autologs.utils.log.Logger
import io.reactivex.Observable
import javax.inject.Inject

class MaintenanceViewModel @Inject constructor(
    private val serviceRepo: ServiceRepository,
    private val remindersRepo: RemindersRepository,
    private val prefRepo: PreferencesRepository
) : BaseViewModel() {

  val detailsState: MutableLiveData<Resource<CarWork>> = MutableLiveData()
  val listState: MutableLiveData<Resource<List<CarWork>>> = MutableLiveData()
  val submitState: MutableLiveData<State> = MutableLiveData()

  private lateinit var maintenanceLiveData: LiveData<List<CarWork>>
  private lateinit var maintenanceObserver: Observer<List<CarWork>>
  var carId: Long? = null
  var maintenanceId: Long? = null

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
          .subscribe({ maintenance ->
            detailsState.value = Resource.success(maintenance)
          }, { error ->
            detailsState.value = Resource.error(error)
          }))
    }
  }

  fun saveWork(carWork: CarWork, addReminder: Boolean) {
    serviceRepo.saveCarWork(carWork)
      .applySchedulers()
      .doOnSubscribe { submitState.value = State.loading() }
      .doAfterTerminate { submitState.value = State.idle() }
      .doOnComplete {
        if (addReminder) {
          addReminder(carWork)
        } else {
          submitState.value = State.success(carWork)
        }
      }
      .subscribe({
        Logger.i("saveWork subscribe", "saveWork() complete")
      }, {
        submitState.value = State.errorWork(it)
      }).also { addSub(it) }
  }

  private fun addReminder(carWork: CarWork) {
    prefRepo.getPreferenceByCarAndName(carWork.carId, carWork.name)
      .applySchedulers()
      .subscribe({ pref ->
        remindersRepo.addReminder(carWork, pref)
            .applySchedulers()
            .subscribe({
              Logger.i("Save Work -> Add Reminder", "Successfully saved car work and added reminder (if applicable)")
              submitState.value = State.success(carWork)
            }, {
              Logger.d("Save Work -> Add Reminder", "Failed to add a reminder for ${carWork.name}")
              submitState.value = State.errorReminder(it)
            }).also { addSub(it) }
      }, {
        Logger.d("Save Work -> Get Preference", "Failed to get preference/add a reminder for ${carWork.name}")
        submitState.value = State.errorPref(it, carWork)
      }).also { addSub(it) }
  }

  fun addReminderFromManualPref(carWork: CarWork, miles: Int, months: Int) {
    val pref =  Preference(null, carWork.carId, carWork.name, miles, months)
    remindersRepo.addReminder(carWork, pref)
      .applySchedulers()
      .doOnError {
        submitState.value = State.errorReminder(it)
      }
      .subscribe({
        submitState.value = State.success(carWork)
        Logger.i("Save Work -> Add Reminder", "Successfully saved car work and added reminder (if applicable)")
      }, {
        Logger.d("Save Work -> Add Reminder", "Failed to add a reminder for ${carWork.name}")
      }).also { addSub(it) }
  }

  enum class Status {
    IDLE,
    LOADING,
    SUCCESS,
    ERROR_PREF,
    ERROR_REMINDER,
    ERROR_WORK
  }

  data class State(val status: Status,
                   val error: Throwable? = null,
                   val work: CarWork? = null) {

    companion object {
      fun idle() = State(Status.IDLE)
      fun loading() = State(Status.LOADING)
      fun success(work: CarWork) = State(Status.SUCCESS, work = work)
      fun errorPref(error: Throwable, carWork: CarWork) = State(Status.ERROR_PREF, error = error, work = carWork)
      fun errorReminder(error: Throwable) = State(Status.ERROR_REMINDER, error = error)
      fun errorWork(error: Throwable) = State(Status.ERROR_WORK, error = error)
    }
  }
}