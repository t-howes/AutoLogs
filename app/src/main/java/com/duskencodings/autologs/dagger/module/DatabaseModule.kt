package com.duskencodings.autologs.dagger.module

import androidx.room.Room
import android.content.Context
import dagger.Module
import dagger.Provides
import com.duskencodings.autologs.database.AutoDatabase
import com.duskencodings.autologs.database.CarDb
import com.duskencodings.autologs.database.CarWorkDb
import com.duskencodings.autologs.database.PreferencesDb
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