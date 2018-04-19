package sample.thowes.autoservice.base

import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import sample.thowes.autoservice.application.AutoServiceApplication
import sample.thowes.autoservice.database.CarDb
import javax.inject.Inject

open class BaseViewModel : ViewModel() {
  @Inject
  protected lateinit var carDb: CarDb

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