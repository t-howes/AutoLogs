package com.duskencodings.autologs.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

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

  companion object {
    const val TABLE_NAME = "preferences"
  }
}

data class DefaultPreference(val miles: Int, val months: Int? = null)
