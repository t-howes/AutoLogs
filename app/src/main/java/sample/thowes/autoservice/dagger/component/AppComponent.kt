package sample.thowes.autoservice.dagger.component

import dagger.Component
import sample.thowes.autoservice.base.BaseViewModel
import sample.thowes.autoservice.dagger.module.DatabaseModule

@Component(modules = arrayOf(
  DatabaseModule::class
))
interface AppComponent {
  fun inject(baseViewModel: BaseViewModel)
}