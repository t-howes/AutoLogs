package com.duskencodings.autologs.database

import androidx.room.TypeConverter
import com.duskencodings.autologs.models.CarWork
import com.duskencodings.autologs.utils.toDateOrNull
import java.time.LocalDate

class CarWorkTypeConverter {
  @TypeConverter
  fun toString(type: CarWork.Type): String = type.name

  @TypeConverter
  fun fromString(name: String): CarWork.Type? = CarWork.Type.from(name)
}

class DateConverter {
  @TypeConverter
  fun dateToString(date: LocalDate): String = date.toString()

  @TypeConverter
  fun dateFromString(date: String): LocalDate? = date.toDateOrNull()
}