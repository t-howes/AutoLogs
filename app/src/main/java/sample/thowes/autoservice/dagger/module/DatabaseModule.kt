package sample.thowes.autoservice.dagger.module

import android.arch.persistence.room.Room
import android.content.Context
import dagger.Module
import dagger.Provides
import sample.thowes.autoservice.database.AutoDatabase
import sample.thowes.autoservice.database.CarDb
import sample.thowes.autoservice.database.CarWorkDb
import sample.thowes.autoservice.database.PreferencesDb
import javax.inject.Singleton

@Module
@Singleton
class DatabaseModule(val context: Context) {

  private val db: AutoDatabase = Room.databaseBuilder(context,
        AutoDatabase::class.java, "auto-service").fallbackToDestructiveMigration().build()

  @Singleton
  @Provides
  fun provideDatabase(): AutoDatabase = db

  @Singleton
  @Provides
  fun provideCarDb(autoDatabase: AutoDatabase): CarDb {
    return autoDatabase.carDb()
  }

  @Singleton
  @Provides
  fun provideCarWorkDb(autoDatabase: AutoDatabase): CarWorkDb {
    return autoDatabase.carWorkDb()
  }

  @Singleton
  @Provides
  fun providePreferencesDb(autoDatabase: AutoDatabase): PreferencesDb {
    return autoDatabase.preferenceDb()
  }
}