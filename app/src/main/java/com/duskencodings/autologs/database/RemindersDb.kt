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

  @Query("SELECT * FROM $TABLE_NAME WHERE carWorkId = :carWorkId")
  fun getByCarWork(carWorkId: Long): Single<Reminder>

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

  /**
  TABLE
  +---------+----------  +----------+
  | carId   | name       | miles    |
  +---------+------------+----------+
  | 1       |        oil | 100      |
  | 1       | air filter | 150      |
  | 1       |        oil | 200      |
  | 1       |        oil | 250      |
  | 2       |        oil | 100      |
  | 2       | air filter | 200      |
  | 2       |        oil | 400      |
  +---------+------------+----------+

  RESULT
  +---------+----------  +----------+
  | carId   | name       | miles    |
  +---------+------------+----------+
  | 1       | air filter | 150      |
  | 1       |        oil | 250      |
  | 2       | air filter | 200      |
  | 2       |        oil | 400      |
  +---------+------------+----------+
   */
  @Query("""
    SELECT *, MAX(expireAtMiles) FROM $TABLE_NAME
    GROUP BY carId, name
  """)
  fun getNotificationReminders(): Single<List<Reminder>>
}
