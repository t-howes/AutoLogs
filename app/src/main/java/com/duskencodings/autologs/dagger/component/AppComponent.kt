package com.duskencodings.autologs.dagger.component

import dagger.Component
import com.duskencodings.autologs.base.BaseActivity
import com.duskencodings.autologs.base.BaseFragment
import com.duskencodings.autologs.dagger.module.AppModule
import com.duskencodings.autologs.dagger.module.DatabaseModule
import com.duskencodings.autologs.dagger.module.RepositoryModule
import com.duskencodings.autologs.dagger.module.ViewModelModule
import com.duskencodings.autologs.workers.ReminderWorker
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(
    AppModule::class,
    DatabaseModule::class,
    RepositoryModule::class,
    ViewModelModule::class)
)
interface AppComponent {
  fun inject(baseActivity: BaseActivity)
  fun inject(baseFragment: BaseFragment)
  fun inject(worker: ReminderWorker)
}