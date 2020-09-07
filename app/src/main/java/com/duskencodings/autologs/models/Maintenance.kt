package com.duskencodings.autologs.models

import androidx.annotation.StringRes
import com.duskencodings.autologs.R

enum class MaintenanceCategory(@StringRes nameRes: Int) {
  BRAKES_AND_WHEEL(R.string.maintenance_category_brakes_and_wheel),
  COOLING(R.string.maintenance_category_cooling),
  DRIVETRAIN(R.string.maintenance_category_drivetrain),
  ENGINE(R.string.maintenance_category_engine),
  FUEL_AND_AIR(R.string.maintenance_category_fuel_and_air),
  IGNITION(R.string.maintenance_category_ignition),
  SUSPENSION_AND_STEERING(R.string.maintenance_category_suspension_and_steering),
  TRANSMISSION(R.string.maintenance_category_transmission),
  OTHER(R.string.other)
}

data class Job(val name: String, val category: MaintenanceCategory, val defaultPreference: DefaultPreference?)

val maintenanceJobs = listOf(
  Job("Brake pads", MaintenanceCategory.BRAKES_AND_WHEEL, DefaultPreference(60000)),
  Job("Rotors", MaintenanceCategory.BRAKES_AND_WHEEL, DefaultPreference(60000)),
  Job("Tire rotation", MaintenanceCategory.BRAKES_AND_WHEEL, DefaultPreference(10000)),
  Job("Coolant", MaintenanceCategory.COOLING, DefaultPreference(50000, 24)),
  Job("CV Axle", MaintenanceCategory.DRIVETRAIN, DefaultPreference(100000)),
  Job("Oil", MaintenanceCategory.ENGINE, DefaultPreference(3000, 3)),
  Job("Fuel cleaning", MaintenanceCategory.FUEL_AND_AIR, DefaultPreference(3000, 3)),
  Job("Air filter", MaintenanceCategory.FUEL_AND_AIR, DefaultPreference(6000, 6)),
  Job("Spark plugs", MaintenanceCategory.IGNITION, DefaultPreference(50000, 24)),
  Job("Control arms / Lower ball joint", MaintenanceCategory.SUSPENSION_AND_STEERING, DefaultPreference(100000)),
  Job("Tie rod", MaintenanceCategory.SUSPENSION_AND_STEERING, DefaultPreference(60000)),
  Job("Clutch / Assembly", MaintenanceCategory.TRANSMISSION, DefaultPreference(60000)),
  Job("Clutch/transmission fluid", MaintenanceCategory.TRANSMISSION, DefaultPreference(60000, 24)),
  Job("Other", MaintenanceCategory.OTHER, null)
)