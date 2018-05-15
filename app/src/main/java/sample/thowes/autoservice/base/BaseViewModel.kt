package sample.thowes.autoservice.base

import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import sample.thowes.autoservice.application.AutoServiceApplication
import sample.thowes.autoservice.database.CarDb
import sample.thowes.autoservice.database.CarWorkDb
import sample.thowes.autoservice.database.PreferencesDb
import javax.inject.Inject

open class BaseViewModel : ViewModel() {
  @Inject
  protected lateinit var carDb: CarDb

  @Inject
  protected lateinit var carWorkDb: CarWorkDb

  @Inject
  protected lateinit var prefsDb: PreferencesDb

  private val disposables = CompositeDisposable()

  init {
    AutoServiceApplication.component.inject(this)
  }

  protected fun addSub(disposable: Disposable) {
    disposables.add(disposable)
  }

  override fun onCleared() {
    if (!disposables.isDisposed) {
      disposables.dispose()
    }
    super.onCleared()
  }
}