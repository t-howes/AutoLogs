package sample.thowes.autoservice.views.maintenance

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import io.reactivex.Observable
import io.reactivex.Single
import sample.thowes.autoservice.base.BaseViewModel
import sample.thowes.autoservice.extensions.applySchedulers
import sample.thowes.autoservice.models.CarWork

class MaintenanceViewModel : BaseViewModel() {

  var state: MutableLiveData<MaintenanceState> = MutableLiveData()
  private lateinit var maintenanceLiveData: LiveData<List<CarWork>>
  private lateinit var maintenanceObserver: Observer<List<CarWork>>

  enum class MaintenanceStatus {
    IDLE,
    LOADING,
    ERROR,
    NO_MAINTENANCE,
    MAINTENANCE_RETRIEVED,
    MAINTENANCE_LIST_RETRIEVED,
    SUBMIT
  }

  fun getLiveCarWorkRecords(carId: Int? = null, type: Int? = null) {
    carId?.let {
      type?.let {
        maintenanceLiveData = carWorkDb.getLiveCarWorkList(carId, type)
        maintenanceObserver = Observer {
          Single.just(it)
              .applySchedulers()
              .doOnSubscribe { state.postValue(MaintenanceState.loading()) }
              .subscribe({ maintenance ->
                if (maintenance == null || maintenance.isEmpty()) {
                  state.postValue(MaintenanceState.empty())
                } else {
                  state.postValue(MaintenanceState.maintenanceListRetrieved(maintenance))
                }
              }, { error ->
                state.postValue(MaintenanceState.error(error))
              })
        }

        maintenanceLiveData.observeForever(maintenanceObserver)

      } ?: state.postValue(MaintenanceState.idle())
    } ?: state.postValue(MaintenanceState.idle())
  }

  fun getMaintenance(id: Int? = null) {
    id?.let {
      addSub(carWorkDb.getCarWork(id)
          .applySchedulers()
          .doOnSubscribe { state.postValue(MaintenanceState.loading()) }
          .subscribe({ maintenance ->
            state.postValue(MaintenanceState.maintenanceRetrieved(arrayListOf(maintenance)))
          }, { error ->
            state.postValue(MaintenanceState.error(error))
          }))
    } ?: state.postValue(MaintenanceState.idle())
  }

  fun saveCarWork(carWork: CarWork) {
    addSub(Observable.fromCallable {
       carWorkDb.saveWork(carWork)
      }.applySchedulers()
        .doOnSubscribe { state.postValue(MaintenanceState.loading()) }
        .subscribe({
          state.postValue(MaintenanceState.submit())
        }, {
          state.postValue(MaintenanceState.error(it))
        }))
  }

  class MaintenanceState(val status: MaintenanceStatus,
                         val error: Throwable? = null,
                         val maintenance: List<CarWork>? = null) {

    companion object {
      fun idle(): MaintenanceState {
        return MaintenanceState(MaintenanceStatus.IDLE)
      }

      fun loading(): MaintenanceState {
        return MaintenanceState(MaintenanceStatus.LOADING)
      }

      fun error(error: Throwable): MaintenanceState {
        return MaintenanceState(MaintenanceStatus.ERROR, error)
      }

      fun empty(): MaintenanceState {
        return MaintenanceState(MaintenanceStatus.NO_MAINTENANCE)
      }

      fun maintenanceRetrieved(maintenance: List<CarWork>): MaintenanceState {
        return MaintenanceState(MaintenanceStatus.MAINTENANCE_RETRIEVED, maintenance = maintenance)
      }

      fun maintenanceListRetrieved(maintenance: List<CarWork>): MaintenanceState {
        return MaintenanceState(MaintenanceStatus.MAINTENANCE_LIST_RETRIEVED, maintenance = maintenance)
      }

      fun submit(): MaintenanceState {
        return MaintenanceState(MaintenanceStatus.SUBMIT)
      }
    }
  }
}