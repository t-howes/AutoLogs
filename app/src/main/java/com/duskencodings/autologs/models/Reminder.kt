package com.duskencodings.autologs.models

import androidx.room.*
import com.duskencodings.autologs.database.RemindersDb
import com.duskencodings.autologs.utils.toDateOrNull
import java.time.LocalDate

@Entity(tableName = RemindersDb.TABLE_NAME,
        foreignKeys = [(ForeignKey(entity = Car::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("carId"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.NO_ACTION))])
data class Reminder(@PrimaryKey(autoGenerate = true)
                    val id: Int?,
                    val carId: Int,
                    val name: String,
                    val description: String,
                    val type: ReminderType = ReminderType.BASIC,
                    val currentMiles: Int,
                    val currentDate: LocalDate,
                    val expireMiles: Int,
                    val expireDate: LocalDate)

enum class ReminderType {
  BASIC,
  UPCOMING_MAINTENANCE;

  companion object {
    fun fromName(name: String): ReminderType? {
      return values().find { it.name == name }
    }
  }
}

class ReminderTypeConverter {
  @TypeConverter
  fun typeToString(type: ReminderType): String = type.name

  @TypeConverter
  fun typeFromString(name: String): ReminderType? = ReminderType.fromName(name)
}