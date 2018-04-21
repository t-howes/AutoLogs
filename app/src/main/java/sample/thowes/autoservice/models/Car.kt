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
               var maintenance: List<Maintenance> = arrayListOf(),
               var modifications: List<Modification> = arrayListOf(),
               var lastUpdate: String? = Calendar.getInstance().now()) {

  companion object {
    const val TABLE_NAME = "cars"
  }
}

fun Car.totalCost(): Double {
  val maintenanceCost = maintenance.sumByDouble { it.cost ?: 0.0 }
  val modificationCost = modifications.sumByDouble { it.cost ?: 0.0 }
  return maintenanceCost + modificationCost
}

fun Car.yearMakeModel() = "$year $make $model"

abstract class CarWork(var name: String,
                       var date: String,
                       var cost: Double? = 0.0,
                       var milesPerformed: String,
                       var notes: String? = null) {

  enum class Type {
    MAINTENANCE,
    MODIFICATION
  }

  abstract fun getType(): Type
}

class Maintenance(name: String,
                  date: String,
                  cost: Double? = 0.0,
                  milesPerformed: String,
                  notes: String? = null) : CarWork(name, date, cost, milesPerformed, notes) {

  override fun getType() = Type.MAINTENANCE
}

class Modification(name: String,
                   date: String,
                   cost: Double? = 0.0,
                   milesPerformed: String,
                   notes: String? = null) : CarWork(name, date, cost, milesPerformed, notes) {

  override fun getType() = Type.MODIFICATION
}