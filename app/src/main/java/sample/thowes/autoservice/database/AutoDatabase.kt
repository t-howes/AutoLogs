package sample.thowes.autoservice.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import sample.thowes.autoservice.models.Car


@Database(entities = arrayOf(
    Car::class
  ),
  version = 1, exportSchema = false)
abstract class AutoDatabase : RoomDatabase() {
  abstract fun carDb(): CarDb
}