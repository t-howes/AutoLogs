package sample.thowes.autoservice.database.typeConverters

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import sample.thowes.autoservice.models.Maintenance
import sample.thowes.autoservice.models.Modification

class CarWorkTypeConverter {

  @TypeConverter
  fun fromMaintenanceToJson(maintenance: List<Maintenance>): String {
    return Gson().toJson(maintenance)
  }

  @TypeConverter
  fun fromModificationsToJson(modifications: List<Modification>): String {
    return Gson().toJson(modifications)
  }

  @TypeConverter
  fun fromMaintenanceJsonToList(json: String): List<Maintenance> {
    return Gson().fromJson(json, object: TypeToken<List<Maintenance>>() {}.type)
  }

  @TypeConverter
  fun fromModificationJsonToList(json: String): List<Modification> {
    return Gson().fromJson(json, object: TypeToken<List<Modification>>() {}.type)
  }
}