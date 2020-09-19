package com.duskencodings.autologs.models

import android.os.Parcelable
import androidx.room.*
import com.duskencodings.autologs.database.RemindersDb
import com.duskencodings.autologs.models.Reminder.Companion.TABLE_NAME
import com.duskencodings.autologs.utils.formatted
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate
import java.util.*

@Parcelize
@Entity(tableName = TABLE_NAME,
        foreignKeys = [(ForeignKey(entity = Car::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("carId"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.NO_ACTION))])
data class Reminder(@PrimaryKey(autoGenerate = true)
                    val id: Long?,
                    val carId: Long ,
                    val name: String,
                    val description: String,
                    val type: ReminderType = ReminderType.BASIC,
                    val currentMiles: Int,
                    val currentDate: LocalDate,
                    val expireAtMiles: Int,
                    val expireAtDate: LocalDate?): Parcelable {

  companion object {
    const val TABLE_NAME = "reminders"
  }

  fun pushNotificationText(): String {
    var message = "Don't forget to service your ${name.toLowerCase(Locale.getDefault())} at $expireAtMiles miles"

    expireAtDate?.formatted()?.let { date ->
      message += " or by $date"
    }

    return "$message."
  }
}

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