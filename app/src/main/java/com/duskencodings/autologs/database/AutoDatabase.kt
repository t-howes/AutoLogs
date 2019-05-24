package com.duskencodings.autologs.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.duskencodings.autologs.models.Car
import com.duskencodings.autologs.models.CarWork
import com.duskencodings.autologs.models.Preference


@Database(entities = arrayOf(
    Car::class,
    CarWork::class,
    Preference::class
  ),
  version = 1, exportSchema = false)
//@TypeConverters(CarWorkTypeConverter::class)
abstract class AutoDatabase : RoomDatabase() {
  abstract fun carDb(): CarDb
  abstract fun carWorkDb(): CarWorkDb
  abstract fun preferenceDb(): PreferencesDb
  abstract fun remindersDb(): RemindersDb
}