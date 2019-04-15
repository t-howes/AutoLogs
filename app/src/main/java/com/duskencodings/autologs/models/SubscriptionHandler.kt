package com.duskencodings.autologs.models

import io.reactivex.disposables.Disposable

interface SubscriptionHandler {
  fun addSub(disposable: Disposable)
  fun clearDisposables()
}