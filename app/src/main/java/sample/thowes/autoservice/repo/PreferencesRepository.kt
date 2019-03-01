package sample.thowes.autoservice.repo

import android.content.Context
import androidx.lifecycle.LiveData
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import sample.thowes.autoservice.base.BaseRepository
import sample.thowes.autoservice.database.PreferencesDb
import sample.thowes.autoservice.models.Preference

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


}