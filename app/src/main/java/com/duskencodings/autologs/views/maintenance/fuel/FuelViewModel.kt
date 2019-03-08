package com.duskencodings.autologs.views.maintenance.fuel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.duskencodings.autologs.base.BaseViewModel
import com.duskencodings.autologs.models.FuelRecord
import com.duskencodings.autologs.models.Resource

class FuelViewModel : BaseViewModel() {

  val fuelState: MutableLiveData<Resource<FuelRecord>> = MutableLiveData()
  val fuelListState: MutableLiveData<Resource<List<FuelRecord>>> = MutableLiveData()

  private lateinit var fuelListLiveData: LiveData<List<FuelRecord>>
  private lateinit var fuelListObserver: Observer<List<FuelRecord>>

  fun getLiveFuelRecords(carId: Int? = null) {
    carId?.let {
//      fuelListLiveData = carWorkDb.getLiveFuelRecords(carId)
//      fuelListObserver = Observer {
//        Single.just(it)
//            .applySchedulers()
//            .doOnSubscribe { fuelListState.value = Resource.loading() }
//            .doAfterTerminate { fuelListState.value = Resource.idle() }
//            .flatMap { Single.just(it.sortedByDescending { it.date }
//                .sortedByDescending { it.odometerReading }) }
//            .subscribe({ records ->
//              fuelListState.value = Resource.success(records)
//            }, { error ->
//              fuelListState.value = Resource.error(error)
//            })
//      }
//
//      fuelListLiveData.observeForever(fuelListObserver)
    } ?: {
      fuelListState.value = Resource.idle()
      fuelListState.value = Resource.error(NullPointerException("null carId when getting fuel records"))
    }.invoke()
  }
}