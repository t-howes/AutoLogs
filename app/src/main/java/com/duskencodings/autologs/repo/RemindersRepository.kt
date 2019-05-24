package com.duskencodings.autologs.repo

import android.content.Context
import com.duskencodings.autologs.base.BaseRepository
import com.duskencodings.autologs.database.RemindersDb
import com.duskencodings.autologs.models.Reminder
import io.reactivex.Single

class RemindersRepository(context: Context, private val db: RemindersDb) : BaseRepository(context) {

  fun getUpcomingReminders(carId: Int): Single<List<Reminder>> {
    //TODO
    return Single.just(listOf())
  }

}