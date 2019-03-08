package sample.thowes.autoservice.repo

import android.content.Context
import androidx.lifecycle.LiveData
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import sample.thowes.autoservice.base.BaseRepository
import sample.thowes.autoservice.database.CarDb
import sample.thowes.autoservice.models.Car

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