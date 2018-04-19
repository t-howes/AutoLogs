package sample.thowes.autoservice.database

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import io.reactivex.Single
import sample.thowes.autoservice.models.Car

@Dao
interface CarDb {
  @Query("SELECT * FROM ${Car.TABLE_NAME}")
  fun getCars(): Single<List<Car>>

  @Query("SELECT * FROM ${Car.TABLE_NAME} WHERE year=:year AND make=:make AND model=:model")
  fun getCar(year: Int, make: String, model: String): Single<Car>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun saveCar(car: Car)

  @Delete
  fun deleteCar(car: Car)
}