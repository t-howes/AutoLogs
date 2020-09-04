package com.duskencodings.autologs.repo

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import com.duskencodings.autologs.base.BaseRepository
import com.duskencodings.autologs.database.CarWorkDb
import com.duskencodings.autologs.extensions.applySchedulers
import com.duskencodings.autologs.models.CarWork
import com.duskencodings.autologs.models.Resource
import com.duskencodings.autologs.models.SpendingBreakdown
import io.reactivex.Observable

class ServiceRepository(context: Context,
                        private val carWorkDb: CarWorkDb) : BaseRepository(context) {

  fun getLiveCarWorkList(carId: Int): LiveData<List<CarWork>> {
    return carWorkDb.getLiveCarWorkList(carId)
  }

  fun getCarWork(id: Int): Single<CarWork> {
    return carWorkDb.getCarWork(id)
  }

  fun saveCarWork(carWork: CarWork): Completable {
    return Completable.fromCallable {
      carWorkDb.insertOrUpdateCarWork(carWork)
    }.subscribeOn(Schedulers.io())
  }
}