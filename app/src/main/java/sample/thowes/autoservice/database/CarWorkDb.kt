package sample.thowes.autoservice.database

import androidx.lifecycle.LiveData
import androidx.room.*
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

  @Insert
  fun addCarWork(work: CarWork)

  @Update(onConflict = OnConflictStrategy.REPLACE)
  fun saveCarWork(work: CarWork)

  @Delete
  fun deleteWork(work: CarWork)

  @Transaction
  fun insertOrUpdateCarWork(work: CarWork) {
    if (work.id == null) {
      addCarWork(work)
    } else {
      saveCarWork(work)
    }
  }
}