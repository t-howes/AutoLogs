package sample.thowes.autoservice.application

import android.app.Application
import sample.thowes.autoservice.dagger.component.AppComponent
import sample.thowes.autoservice.dagger.component.DaggerAppComponent
import sample.thowes.autoservice.dagger.injector.Injector
import sample.thowes.autoservice.dagger.module.AppModule
import sample.thowes.autoservice.dagger.module.DatabaseModule
import sample.thowes.autoservice.dagger.module.RepositoryModule
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric




class AutoServiceApplication : Application() {

  override fun onCreate() {
    super.onCreate()
    initDagger()
    Fabric.with(this, Crashlytics())
  }

  private fun initDagger() {
    Injector.component = DaggerAppComponent
        .builder()
        .appModule(AppModule(this))
        .databaseModule(DatabaseModule(this))
        .repositoryModule(RepositoryModule())
        .build()
  }
}