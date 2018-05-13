package sample.thowes.autoservice.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey

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
                      var months: String) {

  companion object {
    const val TABLE = "preferences"

  }
}