package com.duskencodings.autologs.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.duskencodings.autologs.models.*


@Database(entities = arrayOf(
    Car::class,
    CarWork::class,
    Preference::class,
    Reminder::class
  ),
  version = 1, exportSchema = false)
@TypeConverters(ReminderTypeConverter::class, CarWorkTypeConverter::class)
abstract class AutoDatabase : RoomDatabase() {
  abstract fun carDb(): CarDb
  abstract fun carWorkDb(): CarWorkDb
  abstract fun preferenceDb(): PreferencesDb
  abstract fun remindersDb(): RemindersDb
}