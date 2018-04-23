package sample.thowes.autoservice.views.maintenance

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import io.reactivex.Single
import sample.thowes.autoservice.base.BaseViewModel
import sample.thowes.autoservice.extensions.applySchedulers
import sample.thowes.autoservice.models.CarWork

class MaintenanceViewModel : BaseViewModel() {

  var state: MutableLiveData<MaintenanceListState> = MutableLiveData()
  private lateinit var maintenanceLiveData: LiveData<List<CarWork>>
  private lateinit var maintenanceObserver: Observer<List<CarWork>>

  enum class MaintenanceStatus {
    IDLE,
    LOADING,
    ERROR,
    NO_MAINTENANCE,
    MAINTENANCE_RETRIEVED,
    MAINTENANCE_LIST_RETRIEVED
  }

  fun getLiveCarWorkRecords(carId: Int? = null, type: Int? = null) {
    carId?.let {
      type?.let {
        maintenanceLiveData = carWorkDb.getLiveCarWorkList(carId, type)
        maintenanceObserver = Observer {
          Single.just(it)
              .applySchedulers()
              .doOnSubscribe { state.postValue(MaintenanceListState.loading()) }
              .subscribe({ maintenance ->
                if (maintenance == null || maintenance.isEmpty()) {
                  state.postValue(MaintenanceListState.empty())
                } else {
                  state.postValue(MaintenanceListState.maintenanceListRetrieved(maintenance))
                }
              }, { error ->
                state.postValue(MaintenanceListState.error(error))
              })
        }

        maintenanceLiveData.observeForever(maintenanceObserver)

      } ?: state.postValue(MaintenanceListState.idle())
    } ?: state.postValue(MaintenanceListState.idle())
  }

  fun getMaintenance(id: Int? = null) {
    id?.let {
      addSub(carWorkDb.getCarWork(id)
          .applySchedulers()
          .doOnSubscribe { state.postValue(MaintenanceListState.loading()) }
          .subscribe({ maintenance ->
            state.postValue(MaintenanceListState.maintenanceRetrieved(arrayListOf(maintenance)))
          }, { error ->
            state.postValue(MaintenanceListState.error(error))
          }))
    } ?: state.postValue(MaintenanceListState.idle())
  }

  class MaintenanceListState(val status: MaintenanceStatus,
                             val error: Throwable? = null,
                             val maintenance: List<CarWork>? = null) {

    companion object {
      fun idle(): MaintenanceListState {
        return MaintenanceListState(MaintenanceStatus.IDLE)
      }

      fun loading(): MaintenanceListState {
        return MaintenanceListState(MaintenanceStatus.LOADING)
      }

      fun error(error: Throwable): MaintenanceListState {
        return MaintenanceListState(MaintenanceStatus.ERROR, error)
      }

      fun empty(): MaintenanceListState {
        return MaintenanceListState(MaintenanceStatus.NO_MAINTENANCE)
      }

      fun maintenanceRetrieved(maintenance: List<CarWork>): MaintenanceListState {
        return MaintenanceListState(MaintenanceStatus.MAINTENANCE_RETRIEVED, maintenance = maintenance)
      }

      fun maintenanceListRetrieved(maintenance: List<CarWork>): MaintenanceListState {
        return MaintenanceListState(MaintenanceStatus.MAINTENANCE_LIST_RETRIEVED, maintenance = maintenance)
      }
    }
  }
}