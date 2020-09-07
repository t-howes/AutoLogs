package com.duskencodings.autologs.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.duskencodings.autologs.models.Reminder
import com.duskencodings.autologs.models.Reminder.Companion.TABLE_NAME
import io.reactivex.Single

@Dao
interface RemindersDb {

  @Query("SELECT * FROM $TABLE_NAME")
  fun getAllReminders(): Single<List<Reminder>>

  @Query("SELECT * FROM $TABLE_NAME WHERE carId = :carId")
  fun getReminders(carId: Long): Single<List<Reminder>>

  @Query("SELECT * FROM $TABLE_NAME WHERE carId = :carId")
  fun getLiveReminders(carId: Long): LiveData<List<Reminder>>

  @Query("SELECT * FROM $TABLE_NAME WHERE id = :id")
  fun getLiveReminder(id: Long?): LiveData<Reminder>

  @Query("SELECT * FROM $TABLE_NAME WHERE id = :id")
  fun getReminder(id: Long): Single<Reminder>

  @Query("SELECT * FROM $TABLE_NAME WHERE carId = :carId AND name = :jobName")
  fun getReminderForCarByJobName(carId: Long, jobName: String): Single<Reminder>

  @Insert
  fun addReminder(reminder: Reminder): Long

  @Update(onConflict = OnConflictStrategy.REPLACE)
  fun updateReminder(reminder: Reminder)

  @Transaction
  fun insertOrUpdate(reminder: Reminder): Reminder {
    return if (reminder.id == null) {
      val id = addReminder(reminder)
      reminder.copy(id = id)
    } else {
      updateReminder(reminder)
      reminder
    }
  }

  @Delete
  fun deleteReminder(reminder: Reminder)

  @Query("""
    SELECT *, MAX(expireAtMiles) FROM $TABLE_NAME
    WHERE carId = :carId
    GROUP BY name
  """)
  fun getUpcomingReminders(carId: Int): Single<List<Reminder>>
}

