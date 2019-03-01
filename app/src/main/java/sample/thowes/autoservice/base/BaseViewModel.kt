package sample.thowes.autoservice.base

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import sample.thowes.autoservice.application.AutoServiceApplication
import sample.thowes.autoservice.dagger.injector.Injector
import sample.thowes.autoservice.database.CarDb
import sample.thowes.autoservice.database.CarWorkDb
import sample.thowes.autoservice.database.PreferencesDb
import javax.inject.Inject

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