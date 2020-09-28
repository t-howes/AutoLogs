package com.duskencodings.autologs.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import com.duskencodings.autologs.utils.now
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate

@Entity(tableName = Car.TABLE_NAME)
data class Car(@PrimaryKey(autoGenerate = true)
               val id: Long?,
               var year: Int,
               var make: String,
               var model: String,
               var nickname: String? = null,
               var notes: String? = null,
               var lastUpdate: LocalDate = now()) {

  val name: String
    get() = if (nickname.isNullOrBlank()) yearMakeModel() else nickname!!

  companion object {
    const val TABLE_NAME = "cars"
  }
}

fun Car.yearMakeModel() = "$year $make $model"

@Parcelize
@Entity(tableName = CarWork.TABLE,
        foreignKeys = [(ForeignKey(entity = Car::class,
                                   parentColumns = arrayOf("id"),
                                   childColumns = arrayOf("carId"),
                                   onDelete = CASCADE))])
data class CarWork(@PrimaryKey(autoGenerate = true)
                   val id: Long?,
                   val carId: Long,
                   var name: String,
                   val type: Type,
                   var date: LocalDate,
                   var cost: Double? = 0.0,
                   var miles: Int,
                   var merchant: String?,
                   var notes: String? = null) : Parcelable{

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