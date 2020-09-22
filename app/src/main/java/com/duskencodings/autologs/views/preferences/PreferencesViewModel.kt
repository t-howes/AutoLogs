package com.duskencodings.autologs.views.preferences

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.reactivex.Single
import com.duskencodings.autologs.base.BaseViewModel
import com.duskencodings.autologs.utils.applySchedulers
import com.duskencodings.autologs.models.Preference
import com.duskencodings.autologs.models.Resource
import com.duskencodings.autologs.repo.PreferencesRepository
import javax.inject.Inject

class PreferencesViewModel @Inject constructor(private val repo: PreferencesRepository) : BaseViewModel() {
  var prefsState: MutableLiveData<Resource<List<Preference>>> = MutableLiveData()
  var submitState: MutableLiveData<Resource<Preference>> = MutableLiveData()
  private lateinit var prefsLiveData: LiveData<List<Preference>>
  private lateinit var prefsObserver: Observer<List<Preference>>

  fun getLivePreferences(carId: Long? = null) {
    carId?.let {
      prefsLiveData = repo.getLivePreferencesList(carId)
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
    repo.savePreference(pref)
      .applySchedulers()
      .doOnSubscribe { prefsState.value = Resource.loading() }
      .doAfterTerminate { prefsState.value = Resource.idle() }
      .subscribe({
        submitState.value = Resource.success(it)
      }, {
        submitState.value = Resource.error(it)
      }).also { addSub(it) }
  }
}