package com.duskencodings.autologs.models


open class Reminder(val carId: Int, val name: String, val viewType: ReminderType = ReminderType.BASIC)

enum class ReminderType {
  BASIC,
  MAINTENANCE
}

class MaintenanceReminder(carId: Int,
                          name: String,
                          val currentMiles: Int,
                          val currentDate: String,
                          val expireMiles: Int,
                          val expireDate: String) : Reminder(carId, name, ReminderType.MAINTENANCE)