package com.duskencodings.autologs.dagger.module

import androidx.room.Room
import android.content.Context
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.duskencodings.autologs.database.*
import com.duskencodings.autologs.models.CarWork
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule(val context: Context) {

  private val db: AutoDatabase = Room.databaseBuilder(context, AutoDatabase::class.java, "auto-service")
      .fallbackToDestructiveMigration()
      .addMigrations(
          object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
              database.execSQL("ALTER TABLE ${CarWork.TABLE} ADD COLUMN merchant")
            }
          }
      )
      .build()

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

  @Singleton
  @Provides
  fun provideRemindersDb(autoDatabase: AutoDatabase): RemindersDb {
    return autoDatabase.remindersDb()
  }
}