package com.duskencodings.autologs.application

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.crashlytics.android.Crashlytics
import com.duskencodings.autologs.dagger.component.DaggerAppComponent
import com.duskencodings.autologs.dagger.injector.Injector
import com.duskencodings.autologs.dagger.module.AppModule
import com.duskencodings.autologs.dagger.module.DatabaseModule
import com.duskencodings.autologs.dagger.module.RepositoryModule
import com.duskencodings.autologs.workers.ReminderWorker
import io.fabric.sdk.android.Fabric
import java.util.concurrent.TimeUnit

class AutoServiceApplication : Application() {

  override fun onCreate() {
    super.onCreate()
    initDagger()
    Fabric.with(this, Crashlytics())
    startReminders()
  }

  private fun initDagger() {
    Injector.component = DaggerAppComponent
        .builder()
        .appModule(AppModule(this))
        .databaseModule(DatabaseModule(this))
        .repositoryModule(RepositoryModule())
        .build()
  }

  private fun startReminders() {
//    val work = PeriodicWorkRequest.Builder(ReminderWorker::class.java, ReminderWorker.RUNTIME_INTERVAL, TimeUnit.HOURS).build()
//    WorkManager.getInstance(this).enqueueUniquePeriodicWork("carServiceReminders", ExistingPeriodicWorkPolicy.KEEP, work)
  }
}