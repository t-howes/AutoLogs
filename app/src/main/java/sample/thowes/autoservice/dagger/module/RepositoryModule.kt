package sample.thowes.autoservice.dagger.module

import android.content.Context
import dagger.Module
import dagger.Provides
import sample.thowes.autoservice.database.CarDb
import sample.thowes.autoservice.database.CarWorkDb
import sample.thowes.autoservice.database.PreferencesDb
import sample.thowes.autoservice.repo.CarRepository
import sample.thowes.autoservice.repo.CarWorkRepository
import sample.thowes.autoservice.repo.PreferencesRepository
import javax.inject.Singleton

@Module
class RepositoryModule {

  @Provides
  @Singleton
  internal fun provideCarRepo(context: Context, carDb: CarDb): CarRepository {
    return CarRepository(context, carDb)
  }

  @Provides
  @Singleton
  internal fun provideCarWorkRepo(context: Context, carWorkDb: CarWorkDb): CarWorkRepository {
    return CarWorkRepository(context, carWorkDb)
  }

  @Provides
  @Singleton
  internal fun providePrefsRepo(context: Context, preferencesDb: PreferencesDb): PreferencesRepository {
    return PreferencesRepository(context, preferencesDb)
  }
}