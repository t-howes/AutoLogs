package com.duskencodings.autologs.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import io.reactivex.Single
import com.duskencodings.autologs.models.Car

@Dao
interface CarDb {
  @Query("SELECT * FROM ${Car.TABLE_NAME}")
  fun getCars(): LiveData<List<Car>>

  @Query("SELECT * FROM ${Car.TABLE_NAME} WHERE id = :id")
  fun getLiveCar(id: Int?): LiveData<Car>

  @Query("SELECT * FROM ${Car.TABLE_NAME} WHERE id = :id")
  fun getCar(id: Int?): Single<Car>

  @Insert
  fun addCar(car: Car)

  @Update(onConflict = OnConflictStrategy.REPLACE)
  fun saveCar(car: Car)

  @Transaction
  fun insertOrUpdate(car: Car) {
    if (car.id == null) {
      addCar(car)
    } else {
      saveCar(car)
    }
  }

  @Delete
  fun deleteCar(car: Car)
}