package com.duskencodings.autologs.views.reminders

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.duskencodings.autologs.R
import com.duskencodings.autologs.base.BaseActivity
import com.duskencodings.autologs.base.BaseDialogFragment
import com.duskencodings.autologs.models.REMINDER_ID_DEFAULT
import com.duskencodings.autologs.models.Reminder
import com.duskencodings.autologs.utils.formatted
import com.duskencodings.autologs.utils.now
import com.duskencodings.autologs.utils.visible
import kotlinx.android.synthetic.main.fragment_reminder_details.*
import kotlinx.android.synthetic.main.fragment_reminder_details.loading
import java.time.LocalDate
import java.util.Calendar

class EditReminderDialogFragment : BaseDialogFragment() {

  private lateinit var reminderDetailsViewModel: ReminderDetailsViewModel
  private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
  private var selectedDate: LocalDate = now()

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.fragment_reminder_details, container)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val reminderId = arguments!!.getLong(REMINDER_ID, REMINDER_ID_DEFAULT)
    init(reminderId)
  }

  override fun showLoading(show: Boolean) {
    loading.visible = show
  }

  private fun init(reminderId: Long) {
    initUi()
    reminderDetailsViewModel = getViewModel(this)

    reminderDetailsViewModel.state.observeForever { state ->
      state?.let {
        onStateUpdated(it)
      }
    }

    reminderDetailsViewModel.loadReminder(reminderId)
  }

  private fun onStateUpdated(state: ReminderDetailsViewModel.State) {
    when (state.status) {
      ReminderDetailsViewModel.Status.IDLE -> showLoading(false)
      ReminderDetailsViewModel.Status.LOADING -> showLoading(true)
      ReminderDetailsViewModel.Status.SUCCESS_REMINDER -> {
        state.reminder?.let {
          onReminderReceived(it)
        }
      }
      ReminderDetailsViewModel.Status.DATE_SAVED -> onDateSaved()
      ReminderDetailsViewModel.Status.ERROR -> state.error?.let {
        onError(it)
      }
      ReminderDetailsViewModel.Status.REMINDER_DELETED -> dismiss()
    }
  }

  private fun initUi() {
    change_date.setOnClickListener { showDatePicker() }
    delete.setOnClickListener { showDeleteDialog() }

    dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
      selectedDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
      reminderDetailsViewModel.onDateSelected(selectedDate)
    }
  }

  private fun onDateSaved() {
    date_input.setText(selectedDate.formatted())
    dismiss()
  }

  private fun onReminderReceived(reminder: Reminder) {
    reminder_name.text = reminder.name
    miles.text = getString(R.string.x_miles, reminder.expireAtMiles)
    date_input.setText(reminder.expireAtDate.formatted())
    selectedDate = reminder.expireAtDate
  }

  private fun showDatePicker() {
    date_input.clearFocus()
    DatePickerDialog(requireContext(), dateSetListener,
        selectedDate.year,
        selectedDate.monthValue - 1,
        selectedDate.dayOfMonth
    ).apply {
      datePicker.minDate = Calendar.getInstance().also { cal -> cal.add(Calendar.DATE, 1) }.timeInMillis
    }.show()
  }

  private fun showDeleteDialog() {
    AlertDialog.Builder(requireContext())
        .setTitle(R.string.delete_reminder)
        .setMessage(R.string.confirm_delete_reminder)
        .setPositiveButton(R.string.yes) { _, _ ->
          reminderDetailsViewModel.deleteReminder()
        }
        .setNegativeButton(R.string.cancel) { di, _ ->
          di.dismiss()
        }
        .show()
  }

  companion object {
    private const val REMINDER_ID = "reminderId"

    fun show(context: Context, reminderId: Long) {
      val fragManager = (context as BaseActivity).supportFragmentManager
      EditReminderDialogFragment().apply {
        arguments = Bundle().apply {
          putLong(REMINDER_ID, reminderId)
        }
      }.show(fragManager, EditReminderDialogFragment::class.java.name)
    }
  }
}