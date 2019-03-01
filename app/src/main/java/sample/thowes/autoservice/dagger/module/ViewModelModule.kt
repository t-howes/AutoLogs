package sample.thowes.autoservice.dagger.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import sample.thowes.autoservice.dagger.InjectingViewModelFactory
import sample.thowes.autoservice.dagger.ViewModelKey
import sample.thowes.autoservice.views.cars.details.CarViewModel
import sample.thowes.autoservice.views.cars.list.CarsViewModel
import sample.thowes.autoservice.views.maintenance.MaintenanceViewModel

@Module
abstract class ViewModelModule {

  @Binds
  @IntoMap
  @ViewModelKey(CarViewModel::class)
  abstract fun bindCarViewModel(carViewModel: CarViewModel): ViewModel

  @Binds
  @IntoMap
  @ViewModelKey(CarsViewModel::class)
  abstract fun bindCarsViewModel(carsViewModel: CarsViewModel): ViewModel

  @Binds
  @IntoMap
  @ViewModelKey(MaintenanceViewModel::class)
  abstract fun bindCarWorkViewModel(workViewModel: MaintenanceViewModel): ViewModel

  @Binds
  abstract fun bindViewModelFactory(factory: InjectingViewModelFactory): ViewModelProvider.Factory
}