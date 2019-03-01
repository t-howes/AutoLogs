package sample.thowes.autoservice.database

import androidx.lifecycle.LiveData
import androidx.room.*
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

  @Insert
  fun addPreference(pref: Preference)

  @Update(onConflict = OnConflictStrategy.REPLACE)
  fun updatePreference(pref: Preference)

  @Delete
  fun deletePreference(pref: Preference)

  @Transaction
  fun insertOrUpdatePref(pref: Preference) {
    if (pref.id == null) {
      addPreference(pref)
    } else {
      updatePreference(pref)
    }
  }
}