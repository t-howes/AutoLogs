package com.duskencodings.autologs.application

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.duskencodings.autologs.dagger.component.DaggerAppComponent
import com.duskencodings.autologs.dagger.injector.Injector
import com.duskencodings.autologs.dagger.module.AppModule
import com.duskencodings.autologs.dagger.module.DatabaseModule
import com.duskencodings.autologs.dagger.module.RepositoryModule
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