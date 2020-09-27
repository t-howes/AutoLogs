package com.duskencodings.autologs.views.reminders

import androidx.lifecycle.MutableLiveData
import com.duskencodings.autologs.base.BaseViewModel
import com.duskencodings.autologs.models.Reminder
import com.duskencodings.autologs.repo.RemindersRepository
import com.duskencodings.autologs.utils.applySchedulers
import java.lang.NullPointerException
import java.time.LocalDate
import javax.inject.Inject

class ReminderDetailsViewModel @Inject constructor(
  private val remindersRepo: RemindersRepository
) : BaseViewModel() {

  private var reminderId: Long? = null
  val state = MutableLiveData<State>()

  fun loadReminder(reminderId: Long) {
    this.reminderId = reminderId

    remindersRepo.getReminder(reminderId)
        .applySchedulers()
        .doOnSubscribe { state.value = State.loading() }
        .doAfterTerminate { state.value = State.idle() }
        .subscribe({
          state.value = State.successReminder(it)
        }, { error ->
          state.value = State.error(error)
        }).also { addSub(it) }
  }

  fun onDateSelected(date: LocalDate) {
    reminderId?.let { id ->
      remindersRepo.saveReminderDate(id, date)
          .applySchedulers()
          .doOnSubscribe { state.value = State.loading() }
          .doAfterTerminate { state.value = State.idle() }
          .subscribe({
            state.value = State.dateSaved()
          }, { error ->
            state.value = State.error(error)
          }).also { addSub(it) }
    } ?: run {
      state.value = State.error(NullPointerException("null reminderId -- can't update date"))
    }
  }

  fun deleteReminder() {
    reminderId?.let { id ->
      remindersRepo.deleteReminder(id)
          .applySchedulers()
          .doOnSubscribe { state.value = State.loading() }
          .doAfterTerminate { state.value = State.idle() }
          .subscribe({
            state.value = State.reminderDeleted()
          }, { error ->
            state.value = State.error(error)
          }).also { addSub(it) }
    } ?: run {
      state.value = State.error(NullPointerException("null reminderId -- can't delete reminder"))
    }
  }

  enum class Status {
    IDLE,
    LOADING,
    ERROR,
    SUCCESS_REMINDER,
    DATE_SAVED,
    REMINDER_DELETED
  }

  data class State(val status: Status,
                   val error: Throwable? = null,
                   val reminder: Reminder? = null) {

    companion object {
      fun idle() = State(Status.IDLE)
      fun loading() = State(Status.LOADING)
      fun successReminder(reminder: Reminder) = State(Status.SUCCESS_REMINDER, reminder = reminder)
      fun dateSaved() = State(Status.DATE_SAVED)
      fun reminderDeleted() = State(Status.REMINDER_DELETED)
      fun error(error: Throwable) = State(Status.ERROR, error = error)
    }
  }
}