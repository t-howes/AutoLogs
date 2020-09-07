package com.duskencodings.autologs.database

import androidx.lifecycle.LiveData
import androidx.room.*
import io.reactivex.Single
import com.duskencodings.autologs.models.Preference

@Dao
interface PreferencesDb {
  @Query("SELECT * FROM ${Preference.TABLE_NAME} WHERE carId = :carId")
  fun getLivePreferencesList(carId: Long): LiveData<List<Preference>>

  @Query("SELECT * FROM ${Preference.TABLE_NAME} WHERE carId = :carId")
  fun getPreferencesList(carId: Long): Single<List<Preference>>

  @Query("SELECT * FROM ${Preference.TABLE_NAME} WHERE id = :id")
  fun getPreference(id: Long?): Single<Preference>

  @Query("SELECT * FROM ${Preference.TABLE_NAME} WHERE carId = :carId AND name = :prefName")
  fun getPreferenceByCarAndName(carId: Long, prefName: String): Single<Preference>

  @Insert
  fun addPreference(pref: Preference): Long

  @Update(onConflict = OnConflictStrategy.REPLACE)
  fun updatePreference(pref: Preference)

  @Delete
  fun deletePreference(pref: Preference)

  @Transaction
  fun insertOrUpdatePref(pref: Preference): Preference {
    return if (pref.id == null) {
      val id = addPreference(pref)
      pref.copy(id = id)
    } else {
      updatePreference(pref)
      pref
    }
  }
}