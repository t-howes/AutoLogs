package sample.thowes.autoservice.extensions

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
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

fun Completable.applySchedulers(subscribeOn: Scheduler = Schedulers.io(),
                                       observeOn: Scheduler = AndroidSchedulers.mainThread()): Completable {
  return this
      .subscribeOn(subscribeOn)
      .observeOn(observeOn)
}