package sample.thowes.autoservice.database

import android.arch.persistence.room.*
import io.reactivex.Single
import sample.thowes.autoservice.models.Car

@Dao
interface CarDb {
  @Query("SELECT * FROM ${Car.TABLE_NAME}")
  fun getCars(): Single<List<Car>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun saveCar(car: Car)

  @Delete
  fun deleteCar(car: Car)
}