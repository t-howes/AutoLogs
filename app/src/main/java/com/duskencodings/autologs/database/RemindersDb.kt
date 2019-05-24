package com.duskencodings.autologs.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.duskencodings.autologs.models.Reminder
import io.reactivex.Single

@Dao
interface RemindersDb {
  @Query("SELECT * FROM $TABLE_NAME")
  fun getReminders(): LiveData<List<Reminder>>

  @Query("SELECT * FROM $TABLE_NAME WHERE id = :id")
  fun getLiveReminder(id: Int?): LiveData<Reminder>

  @Query("SELECT * FROM $TABLE_NAME WHERE id = :id")
  fun getReminder(id: Int?): Single<Reminder>

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

  companion object {
    const val TABLE_NAME = "reminders"
  }
}

