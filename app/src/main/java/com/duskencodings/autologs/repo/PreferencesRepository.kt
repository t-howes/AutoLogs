package com.duskencodings.autologs.repo

import android.content.Context
import androidx.lifecycle.LiveData
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import com.duskencodings.autologs.base.BaseRepository
import com.duskencodings.autologs.database.PreferencesDb
import com.duskencodings.autologs.models.Preference
import com.duskencodings.autologs.models.maintenanceJobs
import io.reactivex.Single

class PreferencesRepository(context: Context,
                            private val prefsDb: PreferencesDb) : BaseRepository(context) {

  fun getLivePreferencesList(carId: Int): LiveData<List<Preference>> {
    return prefsDb.getLivePreferencesList(carId)
  }

  fun savePreference(pref: Preference): Completable {
    return Completable.fromCallable {
      prefsDb.insertOrUpdatePref(pref)
    }.subscribeOn(Schedulers.io())
  }

  fun getPreferenceByCarAndName(carId: Int, prefName: String): Single<Preference> {
    return prefsDb.getPreferenceByCarAndName(carId, prefName).onErrorReturn {
      defaultPreference(carId, prefName)
    }
  }

  private fun defaultPreference(carId: Int, prefName: String): Preference {
    return maintenanceJobs.find { it.name.equals(prefName, true) }?.defaultPreference?.let { default ->
      Preference(null, carId, prefName, default.miles, default.months)
    } ?: throw NoSuchElementException("No default preference for '$prefName'")
  }
}