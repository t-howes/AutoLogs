package sample.thowes.autoservice.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import sample.thowes.autoservice.models.Car
import sample.thowes.autoservice.models.CarWork
import sample.thowes.autoservice.models.Preference


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
}