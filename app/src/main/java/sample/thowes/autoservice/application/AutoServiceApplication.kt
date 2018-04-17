package sample.thowes.autoservice.application

import android.app.Application
import io.realm.Realm
import io.realm.Realm.setDefaultConfiguration
import io.realm.RealmConfiguration
import sample.thowes.autoservice.dagger.component.AppComponent


class AutoServiceApplication : Application() {

  override fun onCreate() {
    super.onCreate()
    initRealm()
    initDagger()
  }

  private fun initDagger() {
    component = DaggerAppComponent
        .builder()
        .databaseModule()
        .build()
  }

  private fun initRealm() {
    Realm.init(this)

    val realmConfig = RealmConfiguration.Builder()
        .name("autoservice.realm")
        .deleteRealmIfMigrationNeeded()
        .build()

    Realm.setDefaultConfiguration(realmConfig)
  }

  companion object {
    lateinit var component: AppComponent
  }
}