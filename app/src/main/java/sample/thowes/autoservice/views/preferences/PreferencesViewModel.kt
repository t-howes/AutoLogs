package sample.thowes.autoservice.views.preferences

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import io.reactivex.Observable
import io.reactivex.Single
import sample.thowes.autoservice.base.BaseViewModel
import sample.thowes.autoservice.extensions.applySchedulers
import sample.thowes.autoservice.models.Car
import sample.thowes.autoservice.models.Preference
import sample.thowes.autoservice.models.Resource

class PreferencesViewModel : BaseViewModel() {
  var prefsState: MutableLiveData<Resource<List<Preference>>> = MutableLiveData()
  var submitState: MutableLiveData<Resource<Boolean>> = MutableLiveData()
  private lateinit var prefsLiveData: LiveData<List<Preference>>
  private lateinit var prefsObserver: Observer<List<Preference>>

  fun getLivePreferences(carId: Int? = null) {
    carId?.let {
      prefsLiveData = prefsDb.getLivePreferencesList(carId)
      prefsObserver = Observer {
        Single.just(it)
            .applySchedulers()
            .doOnSubscribe { prefsState.value = Resource.loading() }
            .doAfterTerminate { prefsState.value = Resource.idle() }
            .subscribe({ prefs ->
              prefs?.let {
                prefsState.value = Resource.success(prefs)
              } ?: { prefsState.value = Resource.error(NullPointerException("null prefs from Room")) }.invoke()
            }, { error ->
              prefsState.value = Resource.error(error)
            })

      }

      prefsLiveData.observeForever(prefsObserver)
    } ?: { prefsState.value = Resource.idle() }.invoke()
  }

  fun updatePreference(pref: Preference) {
    addSub(Observable.fromCallable {
      prefsDb.savePreference(pref)
    }.applySchedulers()
        .doOnSubscribe { prefsState.value = Resource.loading() }
        .doAfterTerminate { prefsState.value = Resource.idle() }
        .subscribe({
          submitState.value = Resource.success(true)
        }, {
          submitState.value = Resource.error(it)
        }))
  }
}