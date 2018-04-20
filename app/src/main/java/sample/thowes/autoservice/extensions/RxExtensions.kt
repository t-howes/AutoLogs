package sample.thowes.autoservice.extensions

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


fun <T> Observable<T>.applySchedulers(): Observable<T> {
  return this
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
}

fun <T> Single<T>.applySchedulers(): Single<T> {
  return this
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
}