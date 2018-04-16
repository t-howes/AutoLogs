package sample.thowes.autoservice.base

import android.arch.lifecycle.ViewModel

open class BaseViewModel : ViewModel() {

  override fun onCleared() {
    super.onCleared()
  }
}