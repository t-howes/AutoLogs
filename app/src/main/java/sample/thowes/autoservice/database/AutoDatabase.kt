package sample.thowes.autoservice.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import sample.thowes.autoservice.models.Car
import sample.thowes.autoservice.models.CarWork


@Database(entities = arrayOf(
    Car::class,
    CarWork::class
  ),
  version = 1, exportSchema = false)
//@TypeConverters(CarWorkTypeConverter::class)
abstract class AutoDatabase : RoomDatabase() {
  abstract fun carDb(): CarDb
  abstract fun carWorkDb(): CarWorkDb
}