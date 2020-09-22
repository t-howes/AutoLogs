package com.duskencodings.autologs.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = Preference.TABLE_NAME,
        foreignKeys = [(ForeignKey(entity = Car::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("carId"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.NO_ACTION))])
data class Preference(@PrimaryKey(autoGenerate = true)
                      val id: Long?,
                      val carId: Long,
                      var name: String,
                      var miles: Int,
                      var months: Int?) {

  fun getExpirationDate(carWork: CarWork): LocalDate {
    val months: Double = months?.toString()?.toDoubleOrNull() ?: miles / AVERAGE_MONTHLY_MILES
    return if (months <= 1.0) {
      val days = 30 * months
      carWork.date.plusDays(days.toLong())
    } else {
      carWork.date.plusMonths(months.toLong())
    }
  }

  companion object {
    const val TABLE_NAME = "preferences"
  }
}

data class DefaultPreference(val miles: Int, val months: Int? = null)

const val AVERAGE_MONTHLY_MILES = 1000.0 // TODO: calculate by car's average miles between jobs.
