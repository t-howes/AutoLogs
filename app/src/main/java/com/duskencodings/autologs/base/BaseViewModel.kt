package com.duskencodings.autologs.base

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseViewModel : ViewModel() {

  private val disposables = CompositeDisposable()

  protected fun addSub(disposable: Disposable) {
    disposables.add(disposable)
  }

  override fun onCleared() {
    super.onCleared()

    if (!disposables.isDisposed) {
      disposables.dispose()
    }
  }
}