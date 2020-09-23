package com.duskencodings.autologs.database

import androidx.lifecycle.LiveData
import androidx.room.*
import io.reactivex.Single
import com.duskencodings.autologs.models.CarWork
import io.reactivex.Maybe

@Dao
interface CarWorkDb {

  @Query("SELECT * FROM ${CarWork.TABLE} WHERE carId = :carId")
  fun getLiveCarWorkList(carId: Long): LiveData<List<CarWork>>

  @Query("SELECT * FROM ${CarWork.TABLE} WHERE carId = :carId")
  fun getCarWorkList(carId: Long): Single<List<CarWork>>

  @Query("SELECT * FROM ${CarWork.TABLE} WHERE id = :id")
  fun getCarWork(id: Long): Single<CarWork>

  @Insert
  fun addCarWork(work: CarWork): Long

  @Update(onConflict = OnConflictStrategy.REPLACE)
  fun saveCarWork(work: CarWork)

  @Delete
  fun deleteWork(work: CarWork)

  @Transaction
  fun insertOrUpdateCarWork(work: CarWork): CarWork {
    return if (work.id == null) {
      val id = addCarWork(work)
      work.copy(id = id)
    } else {
      saveCarWork(work)
      work
    }
  }

  @Query(
    """
      SELECT *, MAX(miles) FROM ${CarWork.TABLE}
      WHERE name = :name
    """
  )
  fun getPreviousWork(name: String): Single<CarWork>
}