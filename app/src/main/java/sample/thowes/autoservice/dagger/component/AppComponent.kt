package sample.thowes.autoservice.dagger.component

import dagger.Component
import sample.thowes.autoservice.base.BaseViewModel
import sample.thowes.autoservice.dagger.module.AppModule
import sample.thowes.autoservice.dagger.module.DatabaseModule
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(
    AppModule::class,
    DatabaseModule::class)
)
interface AppComponent {
  fun inject(baseViewModel: BaseViewModel)
}