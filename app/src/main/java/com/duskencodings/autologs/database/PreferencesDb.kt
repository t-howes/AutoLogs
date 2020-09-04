package com.duskencodings.autologs.database

import androidx.lifecycle.LiveData
import androidx.room.*
import io.reactivex.Single
import com.duskencodings.autologs.models.Preference


@Dao
interface PreferencesDb {
  @Query("SELECT * FROM ${Preference.TABLE} WHERE carId = :carId")
  fun getLivePreferencesList(carId: Int): LiveData<List<Preference>>

  @Query("SELECT * FROM ${Preference.TABLE} WHERE carId = :carId")
  fun getPreferencesList(carId: Int): Single<List<Preference>>

  @Query("SELECT * FROM ${Preference.TABLE} WHERE id = :id")
  fun getPreference(id: Int?): Single<Preference>

  @Query("SELECT * FROM ${Preference.TABLE} WHERE carId = :carId AND name = :prefName")
  fun getPreferenceByCarAndName(carId: Int, prefName: String): Single<Preference>

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