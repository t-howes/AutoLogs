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
import com.duskencodings.autologs.models.Reminder

@Dao
interface CarDb {
  @Query("SELECT * FROM ${Car.TABLE_NAME}")
  fun getLiveCars(): LiveData<List<Car>>

  @Query("SELECT * FROM ${Car.TABLE_NAME}")
  fun getCars(): Single<List<Car>>

  @Query("SELECT * FROM ${Car.TABLE_NAME} WHERE id = :id")
  fun getLiveCar(id: Long): LiveData<Car>

  @Query("SELECT * FROM ${Car.TABLE_NAME} WHERE id = :id")
  fun getCar(id: Long): Single<Car>

  @Insert
  fun addCar(car: Car): Long

  @Update(onConflict = OnConflictStrategy.REPLACE)
  fun saveCar(car: Car)

  @Transaction
  fun insertOrUpdate(car: Car): Car {
    return if (car.id == null) {
      val id = addCar(car)
      car.copy(id = id)
    } else {
      saveCar(car)
      car
    }
  }

  @Delete
  fun deleteCar(car: Car)

  @Query("DELETE FROM ${Car.TABLE_NAME} WHERE id = :carId")
  fun deleteCar(carId: Long)
}