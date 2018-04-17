package sample.thowes.autoservice.models

import android.arch.persistence.room.Entity
import sample.thowes.autoservice.extensions.today
import java.util.*

@Entity(tableName = Car.TABLE_NAME, primaryKeys = arrayOf("year", "make", "model"))
data class Car(val name: String? = null,
               val year: Int,
               val make: String,
               val model: String,
               val miles: Int? = 0,
               val lastUpdate: String? = Calendar.getInstance().today()) {

  companion object {
    const val TABLE_NAME = "cars"
  }
}