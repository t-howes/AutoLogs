package sample.thowes.autoservice.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import sample.thowes.autoservice.extensions.now
import sample.thowes.autoservice.extensions.today
import java.util.*

@Entity(tableName = Car.TABLE_NAME)
data class Car(@PrimaryKey(autoGenerate = true)
               val id: Int?,
               var year: Int,
               var make: String,
               var model: String,
               var miles: Int? = 0,
               var name: String? = null,
               var notes: String? = null,
               var lastUpdate: String? = Calendar.getInstance().now()) {

  companion object {
    const val TABLE_NAME = "cars"
  }
}