package sample.thowes.autoservice.database

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import io.reactivex.Single
import sample.thowes.autoservice.models.Preference


@Dao
interface PreferencesDb {
  @Query("SELECT * FROM ${Preference.TABLE} WHERE carId = :carId")
  fun getLivePreferencesList(carId: Int): LiveData<List<Preference>>

  @Query("SELECT * FROM ${Preference.TABLE} WHERE carId = :carId")
  fun getPreferencesList(carId: Int): Single<List<Preference>>

  @Query("SELECT * FROM ${Preference.TABLE} WHERE id = :id")
  fun getPreference(id: Int?): Single<Preference>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun savePreference(pref: Preference)

  @Delete
  fun deletePreference(pref: Preference)
}