package com.duskencodings.autologs.utils

import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


fun <T> Observable<T>.applySchedulers(subscribeOn: Scheduler = Schedulers.io(),
                                      observeOn: Scheduler = AndroidSchedulers.mainThread()): Observable<T> {
  return this
      .subscribeOn(subscribeOn)
      .observeOn(observeOn)
}

fun <T> Single<T>.applySchedulers(subscribeOn: Scheduler = Schedulers.io(),
                                  observeOn: Scheduler = AndroidSchedulers.mainThread()): Single<T> {
  return this
      .subscribeOn(subscribeOn)
      .observeOn(observeOn)
}

fun <T> Maybe<T>.applySchedulers(subscribeOn: Scheduler = Schedulers.io(),
                                 observeOn: Scheduler = AndroidSchedulers.mainThread()): Maybe<T> {
  return this
      .subscribeOn(subscribeOn)
      .observeOn(observeOn)
}

fun Completable.applySchedulers(subscribeOn: Scheduler = Schedulers.io(),
                                       observeOn: Scheduler = AndroidSchedulers.mainThread()): Completable {
  return this
      .subscribeOn(subscribeOn)
      .observeOn(observeOn)
}