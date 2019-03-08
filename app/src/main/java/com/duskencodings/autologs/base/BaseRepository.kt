package com.duskencodings.autologs.base

import android.content.Context
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseRepository(val context: Context) {

  private var disposables = CompositeDisposable()

  open fun onCleared() {
    clearDisposables()
  }

  fun addDisposable(disposable: Disposable?) {
    disposable?.let { disposables.add(it) }
  }

  open fun clearDisposables() {
    disposables.clear()
  }
}