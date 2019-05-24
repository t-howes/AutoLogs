package com.duskencodings.autologs.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.duskencodings.autologs.database.RemindersDb

@Entity(tableName = RemindersDb.TABLE_NAME,
        foreignKeys = [(ForeignKey(entity = Car::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("carId"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.NO_ACTION))])
open class Reminder(@PrimaryKey(autoGenerate = true)
                    val id: Int?,
                    val carId: Int,
                    val name: String,
                    val viewType: ReminderType = ReminderType.BASIC)

enum class ReminderType {
  BASIC,
  MAINTENANCE
}

class MaintenanceReminder(carId: Int,
                          name: String,
                          val currentMiles: Int,
                          val currentDate: String,
                          val expireMiles: Int,
                          val expireDate: String) : Reminder(null, carId, name, ReminderType.MAINTENANCE)