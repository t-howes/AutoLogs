package sample.thowes.autoservice.dagger.component

import dagger.Component
import sample.thowes.autoservice.base.BaseActivity
import sample.thowes.autoservice.base.BaseFragment
import sample.thowes.autoservice.base.BaseViewModel
import sample.thowes.autoservice.dagger.module.AppModule
import sample.thowes.autoservice.dagger.module.DatabaseModule
import sample.thowes.autoservice.dagger.module.RepositoryModule
import sample.thowes.autoservice.dagger.module.ViewModelModule
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
}