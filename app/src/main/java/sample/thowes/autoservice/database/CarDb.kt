package sample.thowes.autoservice.database

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import io.reactivex.Single
import sample.thowes.autoservice.models.Car

@Dao
interface CarDb {
  @Query("SELECT * FROM ${Car.TABLE_NAME}")
  fun getCars(): LiveData<List<Car>>

  @Query("SELECT * FROM ${Car.TABLE_NAME} WHERE id = :id")
  fun getLiveCar(id: Int?): LiveData<Car>

  @Query("SELECT * FROM ${Car.TABLE_NAME} WHERE id = :id")
  fun getCar(id: Int?): Single<Car>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun saveCar(car: Car)

  @Delete
  fun deleteCar(car: Car)
}