package sample.thowes.autoservice.database

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import io.reactivex.Single
import sample.thowes.autoservice.models.CarWork

@Dao
interface CarWorkDb {

  @Query("SELECT * FROM ${CarWork.TABLE} WHERE carId = :carId")
  fun getLiveCarWorkList(carId: Int): LiveData<List<CarWork>>

  @Query("SELECT * FROM ${CarWork.TABLE} WHERE carId = :carId")
  fun getCarWorkList(carId: Int): Single<List<CarWork>>

  @Query("SELECT * FROM ${CarWork.TABLE} WHERE id = :id")
  fun getCarWork(id: Int?): Single<CarWork>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun saveWork(work: CarWork)

  @Delete
  fun deleteWork(work: CarWork)
}