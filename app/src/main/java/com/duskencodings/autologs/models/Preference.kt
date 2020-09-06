package com.duskencodings.autologs.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.duskencodings.autologs.utils.toDateOrNull
import java.time.LocalDate

@Entity(tableName = Preference.TABLE,
        foreignKeys = [(ForeignKey(entity = Car::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("carId"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.NO_ACTION))])
data class Preference(@PrimaryKey(autoGenerate = true)
                      val id: Int?,
                      val carId: Int,
                      var name: String,
                      var miles: Int,
                      var months: Int) {

  companion object {
    const val TABLE = "preferences"
  }
}