package sample.thowes.autoservice.base

import android.arch.lifecycle.ViewModel
import sample.thowes.autoservice.application.AutoServiceApplication
import sample.thowes.autoservice.database.stub.CarDb
import javax.inject.Inject

open class BaseViewModel : ViewModel() {
  @Inject
  protected lateinit var carDb: CarDb

  init {
    AutoServiceApplication.component.inject(this)
  }

  override fun onCleared() {
    super.onCleared()
  }
}