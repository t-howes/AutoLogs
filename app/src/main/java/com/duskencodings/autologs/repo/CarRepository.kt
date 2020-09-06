package com.duskencodings.autologs.repo

import android.content.Context
import androidx.lifecycle.LiveData
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import com.duskencodings.autologs.base.BaseRepository
import com.duskencodings.autologs.database.CarDb
import com.duskencodings.autologs.models.Car

class CarRepository(context: Context,
                    private val carDb: CarDb): BaseRepository(context) {

  fun getLiveCar(carId: Int): LiveData<Car> {
    return carDb.getLiveCar(carId)
  }

  fun getCars(): LiveData<List<Car>> {
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