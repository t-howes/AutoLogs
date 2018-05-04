package sample.thowes.autoservice.views.maintenance

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import sample.thowes.autoservice.base.BaseViewModel
import sample.thowes.autoservice.extensions.applySchedulers
import sample.thowes.autoservice.log.Logger
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
              .doOnSubscribe { state.value = MaintenanceState.loading() }
              .doAfterTerminate { state.value = MaintenanceState.idle() }
              .flatMap { Single.just(it.sortedByDescending { it.date }) }
              .subscribe({ maintenance ->
                if (maintenance == null || maintenance.isEmpty()) {
                  state.value = MaintenanceState.empty()
                } else {
                  state.value = MaintenanceState.maintenanceListRetrieved(maintenance)
                }
              }, { error ->
                state.value = MaintenanceState.error(error)
              })
        }

        maintenanceLiveData.observeForever(maintenanceObserver)

      } ?: { state.value = MaintenanceState.idle() }.invoke()
    } ?: { state.value = MaintenanceState.idle() }.invoke()
  }

  fun getMaintenance(id: Int? = null) {
    id?.let {
      addSub(carWorkDb.getCarWork(id)
          .applySchedulers()
          .doOnSubscribe { state.value = MaintenanceState.loading() }
          .doAfterTerminate { state.value = MaintenanceState.idle() }
          .subscribe({ maintenance ->
            state.value = MaintenanceState.maintenanceRetrieved(arrayListOf(maintenance))
          }, { error ->
            state.value = MaintenanceState.error(error)
          }))
    } ?: { state.value = MaintenanceState.idle() }.invoke()
  }

  fun updateCar(carWork: CarWork) {
    addSub(carDb.getCar(carWork.carId)
        .applySchedulers()
        .doOnSubscribe { state.value = MaintenanceState.loading() }
        .doAfterTerminate { state.value = MaintenanceState.idle() }
        .observeOn(Schedulers.io())
        // TODO
//        .flatMap { car ->
//          if (carWork.odometerReading > car.miles ?: 0) {
//            car.miles = carWork.odometerReading
//            carDb.saveCar(car)
//          }
//          Single.just(carWork)
//        }
        .flatMap {
          carWorkDb.saveWork(carWork)
          Single.just(carWork)
        }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          state.value = MaintenanceState.submit()
        }, {
          state.value = MaintenanceState.error(it)
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