package com.duskencodings.autologs.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.duskencodings.autologs.models.Reminder
import io.reactivex.Single

@Dao
interface RemindersDb {

  @Query("SELECT * FROM $TABLE_NAME WHERE carId = :carId")
  fun getReminders(carId: Int): Single<List<Reminder>>

  @Query("SELECT * FROM $TABLE_NAME WHERE carId = :carId")
  fun getLiveReminders(carId: Int): LiveData<List<Reminder>>

  @Query("SELECT * FROM $TABLE_NAME WHERE id = :id")
  fun getLiveReminder(id: Int?): LiveData<Reminder>

  @Query("SELECT * FROM $TABLE_NAME WHERE id = :id")
  fun getReminder(id: Int?): Single<Reminder>

  @Query("SELECT * FROM $TABLE_NAME WHERE carId = :carId AND name = :jobName")
  fun getReminderForCarByJobName(carId: Int, jobName: String): Single<Reminder>

  @Insert
  fun addReminder(reminder: Reminder)

  @Update(onConflict = OnConflictStrategy.REPLACE)
  fun updateReminder(reminder: Reminder)

  @Transaction
  fun insertOrUpdate(reminder: Reminder) {
    if (reminder.id == null) {
      addReminder(reminder)
    } else {
      updateReminder(reminder)
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

  companion object {
    const val TABLE_NAME = "reminders"
  }
}

