package com.duskencodings.autologs.repo

import android.content.Context
import androidx.lifecycle.LiveData
import io.reactivex.Single
import com.duskencodings.autologs.base.BaseRepository
import com.duskencodings.autologs.database.CarWorkDb
import com.duskencodings.autologs.models.CarWork
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers

class ServiceRepository(context: Context,
                        private val carWorkDb: CarWorkDb) : BaseRepository(context) {

  fun getLiveCarWorkList(carId: Long): LiveData<List<CarWork>> {
    return carWorkDb.getLiveCarWorkList(carId)
  }

  fun getCarWork(id: Long): Single<CarWork> {
    return carWorkDb.getCarWork(id)
  }

  fun saveCarWork(carWork: CarWork): CarWork {
    return carWorkDb.insertOrUpdateCarWork(carWork)
  }

  fun getPreviousCarWork(name: String): Single<CarWork> {
    return carWorkDb.getPreviousWork(name)
  }

  fun deleteCarWork(carWorkId: Long): Completable {
    return Completable.fromCallable {
      carWorkDb.deleteWork(carWorkId)
    }.subscribeOn(Schedulers.io())
  }
}