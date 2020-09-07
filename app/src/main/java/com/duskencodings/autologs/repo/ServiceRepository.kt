package com.duskencodings.autologs.repo

import android.content.Context
import androidx.lifecycle.LiveData
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import com.duskencodings.autologs.base.BaseRepository
import com.duskencodings.autologs.database.CarWorkDb
import com.duskencodings.autologs.models.CarWork

class ServiceRepository(context: Context,
                        private val carWorkDb: CarWorkDb) : BaseRepository(context) {

  fun getLiveCarWorkList(carId: Long): LiveData<List<CarWork>> {
    return carWorkDb.getLiveCarWorkList(carId)
  }

  fun getCarWork(id: Long): Single<CarWork> {
    return carWorkDb.getCarWork(id)
  }

  fun saveCarWork(carWork: CarWork): Completable {
    return Completable.fromCallable {
      carWorkDb.insertOrUpdateCarWork(carWork)
    }.subscribeOn(Schedulers.io())
  }
}