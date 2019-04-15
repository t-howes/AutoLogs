package com.duskencodings.autologs.dagger.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import com.duskencodings.autologs.dagger.InjectingViewModelFactory
import com.duskencodings.autologs.dagger.ViewModelKey
import com.duskencodings.autologs.views.cars.details.CarViewModel
import com.duskencodings.autologs.views.cars.list.CarsViewModel
import com.duskencodings.autologs.views.maintenance.MaintenanceViewModel

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