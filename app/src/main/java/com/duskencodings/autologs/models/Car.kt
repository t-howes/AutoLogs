package com.duskencodings.autologs.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.duskencodings.autologs.utils.now
import java.time.LocalDate
import java.util.*

@Entity(tableName = Car.TABLE_NAME)
data class Car(@PrimaryKey(autoGenerate = true)
               val id: Int?,
               var year: Int,
               var make: String,
               var model: String,
               var nickname: String? = null,
               var notes: String? = null,
               var lastUpdate: String? = Calendar.getInstance().now()) {

  val name: String
    get() = if (nickname.isNullOrBlank()) yearMakeModel() else nickname!!

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
data class CarWork(@PrimaryKey(autoGenerate = true)
                   val id: Int?,
                   val carId: Int,
                   var name: String,
                   val type: Type,
                   var date: LocalDate,
                   var cost: Double? = 0.0,
                   var odometerReading: Int,
                   var notes: String? = null) {

  enum class Type(val value: Int) {
    MAINTENANCE(0),
    MODIFICATION(1);

    companion object {
      fun from(name: String): Type {
        return values().find { it.name == name } ?: MAINTENANCE
      }
    }
  }

  companion object {
    const val TABLE = "car_work"
  }
}

class CarWorkTypeConverter {
  @TypeConverter
  fun toString(type: CarWork.Type): String = type.name

  @TypeConverter
  fun fromString(name: String): CarWork.Type? = CarWork.Type.from(name)
}