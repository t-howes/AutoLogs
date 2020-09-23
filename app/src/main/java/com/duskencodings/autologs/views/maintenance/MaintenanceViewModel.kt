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
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MaintenanceViewModel @Inject constructor(
    private val serviceRepo: ServiceRepository,
    private val remindersRepo: RemindersRepository,
    private val prefRepo: PreferencesRepository
) : BaseViewModel() {

  val listState: MutableLiveData<Resource<List<CarWork>>> = MutableLiveData()
  val state: MutableLiveData<State> = MutableLiveData()

  private lateinit var maintenanceLiveData: LiveData<List<CarWork>>
  private lateinit var maintenanceObserver: Observer<List<CarWork>>
  var carId: Long? = null
  var workId: Long? = null
  var previousWork: CarWork? = null

  fun getLiveCarWorkRecords() {
    carId?.let {
      maintenanceLiveData = serviceRepo.getLiveCarWorkList(it)
      maintenanceObserver = Observer {
        Observable.just(it)
            .applySchedulers()
            .doOnSubscribe { listState.value = Resource.loading() }
            .doAfterTerminate { listState.value = Resource.idle() }
            .debounce(500, TimeUnit.MILLISECONDS)
            .map { workList ->
              // sort by mileage, group by date
              workList.sortedByDescending { work -> work.miles }
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
    workId?.let {
      addSub(
        serviceRepo.getCarWork(it)
          .applySchedulers()
          .doOnSubscribe { state.value = State.loading() }
          .doAfterTerminate { state.value = State.idle() }
          .subscribe({ maintenance ->
            state.value = State.successMaintenance(maintenance)
          }, { error ->
            state.value = State.errorGetWork(error)
          })
      )
    }
  }

  fun saveWork(carWork: CarWork, addReminder: Boolean) {
    Single.just(carWork)
      .subscribeOn(Schedulers.io())
      .map {
        serviceRepo.saveCarWork(carWork)
      }
      .applySchedulers()
      .doOnSubscribe { state.value = State.loading() }
      .doAfterTerminate { state.value = State.idle() }
      .doOnSuccess { work ->
        if (addReminder) {
          fetchPrefAndAddReminder(work)
        } else {
          state.value = State.successSubmit(work)
        }
      }
      .subscribe({
        Logger.i("saveWork subscribe", "saveWork() complete")
      }, {
        state.value = State.errorSaveWork(it)
      }).also { addSub(it) }
  }

  private fun fetchPrefAndAddReminder(carWork: CarWork) {
    prefRepo.getPreferenceByCarAndName(carWork.carId, carWork.name)
      .applySchedulers()
      .subscribe({ pref ->
        addReminder(carWork, pref)
      }, {
        Logger.d("Save Work -> Get Preference", "Failed to get preference/add a reminder for ${carWork.name}")
        state.value = State.errorSavePref(it, carWork)
      }).also { addSub(it) }
  }

  private fun addReminder(carWork: CarWork, pref: Preference) {
    remindersRepo.saveOrCreateReminder(carWork, pref)
        .applySchedulers()
        .subscribe({
          Logger.i("Save Work -> Add Reminder", "Successfully saved car work and added reminder (if applicable)")
          state.value = State.successSubmit(carWork)
        }, {
          Logger.d("Save Work -> Add Reminder", "Failed to add a reminder for ${carWork.name}")
          state.value = State.errorSaveReminder(it)
        }).also { addSub(it) }
  }

  fun addReminderFromManualPref(carWork: CarWork, miles: Int, months: Int) {
    val pref = Preference(null, carWork.carId, carWork.name, miles, months)
    remindersRepo.saveOrCreateReminder(carWork, pref)
      .applySchedulers()
      .doOnError {
        state.value = State.errorSaveReminder(it)
      }
      .subscribe({
        state.value = State.successSubmit(carWork)
        Logger.i("Save Work -> Add Reminder", "Successfully saved car work and added reminder (if applicable)")
      }, {
        Logger.d("Save Work -> Add Reminder", "Failed to add a reminder for ${carWork.name}")
      }).also { addSub(it) }
  }

  fun copyNotesFromPrevious(jobName: String) {
    if (previousWork == null || !previousWork!!.name.equals(jobName, true)) {
      serviceRepo.getPreviousCarWork(jobName)
        .applySchedulers()
        .doOnSubscribe { state.value = State.loading() }
        .doAfterTerminate { state.value = State.idle() }
        .subscribe({ job ->
          previousWork = job
          state.value = State.successPreviousNotes(job.notes)
        }, {
          state.value = State.errorPreviousNotes()
        }).also { addSub(it) }
    } else {
      state.value = State.successPreviousNotes(previousWork?.notes)
    }
  }

  enum class Status {
    IDLE,
    LOADING,
    SUCCESS_MAINTENANCE,
    SUCCESS_SUBMIT,
    SUCCESS_PREVIOUS_NOTES,
    ERROR_SAVE_PREF,
    ERROR_SAVE_REMINDER,
    ERROR_GET_WORK,
    ERROR_SAVE_WORK,
    ERROR_PREVIOUS_NOTES
  }

  data class State(val status: Status,
                   val error: Throwable? = null,
                   val work: CarWork? = null,
                   val notes: String? = null) {

    companion object {
      fun idle() = State(Status.IDLE)
      fun loading() = State(Status.LOADING)
      fun successMaintenance(work: CarWork) = State(Status.SUCCESS_MAINTENANCE, work = work)
      fun successSubmit(work: CarWork) = State(Status.SUCCESS_SUBMIT, work = work)
      fun errorSavePref(error: Throwable, carWork: CarWork) = State(Status.ERROR_SAVE_PREF, error = error, work = carWork)
      fun errorSaveReminder(error: Throwable) = State(Status.ERROR_SAVE_REMINDER, error = error)
      fun errorGetWork(error: Throwable) = State(Status.ERROR_GET_WORK, error = error)
      fun errorSaveWork(error: Throwable) = State(Status.ERROR_SAVE_WORK, error = error)
      fun successPreviousNotes(notes: String?) = State(Status.SUCCESS_PREVIOUS_NOTES, notes = notes)
      fun errorPreviousNotes() = State(Status.ERROR_PREVIOUS_NOTES)
    }
  }
}