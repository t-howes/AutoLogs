package com.duskencodings.autologs.repo

import android.content.Context
import androidx.lifecycle.LiveData
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import com.duskencodings.autologs.base.BaseRepository
import com.duskencodings.autologs.database.CarDb
import com.duskencodings.autologs.models.Car
import io.reactivex.Single

class CarRepository(context: Context,
                    private val carDb: CarDb): BaseRepository(context) {

  fun getLiveCar(carId: Long): LiveData<Car> {
    return carDb.getLiveCar(carId)
  }

  fun getLiveCars(): LiveData<List<Car>> {
    return carDb.getLiveCars()
  }

  fun getCars(): Single<List<Car>> {
    return carDb.getCars()
  }

  fun saveCar(car: Car): Completable {
    return Completable.fromCallable {
      carDb.insertOrUpdate(car)
    }.subscribeOn(Schedulers.io())
  }

  fun deleteCar(car: Car): Completable {
    return Completable.fromCallable {
      carDb.deleteCar(car)
    }.subscribeOn(Schedulers.io())
  }
}