package sample.thowes.autoservice.application

import android.app.Application
import sample.thowes.autoservice.dagger.component.AppComponent
import sample.thowes.autoservice.dagger.component.DaggerAppComponent
import sample.thowes.autoservice.dagger.module.AppModule
import sample.thowes.autoservice.dagger.module.DatabaseModule


class AutoServiceApplication : Application() {

  override fun onCreate() {
    super.onCreate()
    initDagger()
  }

  private fun initDagger() {
    component = DaggerAppComponent
        .builder()
        .appModule(AppModule(this))
        .databaseModule(DatabaseModule(this))
        .build()
  }

  companion object {
    lateinit var component: AppComponent
  }
}