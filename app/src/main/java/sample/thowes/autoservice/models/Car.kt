package sample.thowes.autoservice.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.ForeignKey.CASCADE
import android.arch.persistence.room.PrimaryKey
import sample.thowes.autoservice.extensions.now
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

//fun Car.totalCost(): Double {
//  val maintenanceCost = maintenance.sumByDouble { it.cost ?: 0.0 }
//  val modificationCost = modifications.sumByDouble { it.cost ?: 0.0 }
//  return maintenanceCost + modificationCost
//}

fun Car.yearMakeModel() = "$year $make $model"


@Entity(tableName = CarWork.TABLE,
        foreignKeys = [(ForeignKey(entity = Car::class,
                                   parentColumns = arrayOf("id"),
                                   childColumns = arrayOf("carId"),
                                   onDelete = CASCADE))])
data class CarWork(@PrimaryKey (autoGenerate = true)
                       val id: Int?,
                       val carId: Int,
                       val type: Int,
                       var name: String,
                       var date: String,
                       var cost: Double? = 0.0,
                       var odometerReading: Int,
                       var notes: String? = null) {

  enum class Type(val value: Int) {
    MAINTENANCE(0),
    MODIFICATION(1);

    companion object {
      fun from(type: Int): Type {
        return Type.values().find { it.value == type } ?: MAINTENANCE
      }
    }
  }

  companion object {
    const val TABLE = "car_work"
  }
}