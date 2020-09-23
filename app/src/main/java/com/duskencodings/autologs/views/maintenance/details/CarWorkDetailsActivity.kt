package com.duskencodings.autologs.views.maintenance.details

import android.app.DatePickerDialog
import androidx.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_maintenance_details.*
import com.duskencodings.autologs.R
import com.duskencodings.autologs.base.BaseActivity
import com.duskencodings.autologs.models.maintenanceJobs
import com.duskencodings.autologs.models.CarWork
import com.duskencodings.autologs.utils.*
import com.duskencodings.autologs.utils.log.Logger
import com.duskencodings.autologs.validation.FormValidator
import com.duskencodings.autologs.views.maintenance.MaintenanceViewModel
import com.duskencodings.autologs.views.preferences.PreferenceInputDialogFragment
import com.duskencodings.autologs.views.preferences.PreferenceInputListener
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*


class CarWorkDetailsActivity : BaseActivity(), PreferenceInputListener {

  private lateinit var maintenanceViewModel: MaintenanceViewModel
  private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
  private val calendar = Calendar.getInstance()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_maintenance_details)
    val carId = intent.extras?.getLong(CAR_ID, -1)
    val id = intent.extras?.getLong(MAINTENANCE_ID, ID_DEFAULT)

    maintenanceViewModel = getViewModel()

    if (carId != ID_DEFAULT) {
      maintenanceViewModel.carId = carId
    }
    if (id != ID_DEFAULT) {
      maintenanceViewModel.workId = id
    }

    initUi()

    maintenanceViewModel.state.observe(this, Observer {
      it?.let {
        onStateUpdated(it)
      }
    })

    maintenanceViewModel.getMaintenance()
  }

  private fun initUi() {
    setDisplayHomeAsUpEnabled()
    setupNameSpinner()

    if (maintenanceViewModel.workId == null) {
      val text = getString(R.string.service)
      setTitle(getString(R.string.add_placeholder, text))
      dateInput.setText(nowFormatted())
    } else {
      copyNotesCheckbox.visible = false
      submit.text = getString(R.string.save)
    }

    setupListeners()
    setupLiveValidations()
  }

  private fun setupNameSpinner() {
    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, maintenanceJobs.map { it.name })
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    nameSpinner.adapter = adapter
    nameSpinner.onItemSelectedListener = object : OnItemSelectedListener {
      override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val isOther = adapter.getItem(position) == getString(R.string.other)
        nameLayout.visible = isOther
        aftermarketModificationsCheckbox.visible = isOther

        if (isOther) {
          nameLayout.requestFocus()
        }
      }

      override fun onNothingSelected(parent: AdapterView<*>?) { }
    }
  }

  private fun setupListeners() {
    copyNotesCheckbox.setOnCheckedChangeListener { _, isChecked ->
      if (isChecked && validName()) {
        maintenanceViewModel.copyNotesFromPrevious(getJobName())
      }
    }

    dateInput.onFocusChangeListener = View.OnFocusChangeListener { p0, hasFocus ->
      if (hasFocus) {
        showDatePicker()
      }
    }

    submit.setOnClickListener {
      if (validForm()) {
        saveCarWork()
      }
    }

    dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
      calendar.set(Calendar.YEAR, year)
      calendar.set(Calendar.MONTH, monthOfYear)
      calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
      updateDateInput()
      milesInput.requestFocus()
    }
  }

  private fun updateDateInput() {
    dateInput.setText(LocalDateTime.ofInstant(calendar.toInstant(), calendar.timeZone.toZoneId()).toLocalDate().formatted())
  }

  private fun setupLiveValidations() {
//    val nameObservable = RxTextView.textChanges(nameInput)
//        .map { inputText -> inputText.isNotEmpty() }
//
//    val dateObservable = RxTextView.textChanges(dateInput)
//        .map { inputText -> inputText.isNotEmpty() }
//
//    val milesObservable = RxTextView.textChanges(milesInput)
//        .map { inputText -> inputText.isNotEmpty() }
//
//    addSub(Observable.combineLatest(nameObservable, dateObservable, milesObservable,
//        Function3<Boolean, Boolean, Boolean, Boolean> { isNameValid, isDateValid, areMilesValid ->
//          isNameValid && isDateValid && areMilesValid
//        }).subscribe { isValid ->
//          submit.isEnabled = isValid
//        })
  }

  private fun showDatePicker() {
    dateInput.clearFocus()
    DatePickerDialog(this, dateSetListener,
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)).apply {
          datePicker.maxDate = Calendar.getInstance().also { cal -> cal.add(Calendar.DAY_OF_MONTH, 1) }.timeInMillis
        }.show()
  }

  private fun onStateUpdated(state: MaintenanceViewModel.State) {
    when (state.status) {
      MaintenanceViewModel.Status.IDLE -> showLoading(false)
      MaintenanceViewModel.Status.LOADING -> showLoading()
      MaintenanceViewModel.Status.SUCCESS_MAINTENANCE -> {
        state.work?.let {
          showMaintenanceDetails(it)
        }
      }
      MaintenanceViewModel.Status.SUCCESS_SUBMIT -> finish()
      MaintenanceViewModel.Status.ERROR_SAVE_PREF -> {
        state.error?.let {
          Logger.d("ERROR_PREF", "Failed to get preference. Set manually...")
        }

        state.work?.let { showManualPreferenceInput(it) }
      }
      MaintenanceViewModel.Status.ERROR_SAVE_REMINDER,
      MaintenanceViewModel.Status.ERROR_SAVE_WORK -> state.error?.let {
        Logger.d("SAVE WORK ERROR", "Failed to save work / add reminder.")
        onError(it)
      }
      MaintenanceViewModel.Status.ERROR_GET_WORK -> state.error?.let {
        showToast(it.localizedMessage)
      }
      MaintenanceViewModel.Status.SUCCESS_PREVIOUS_NOTES -> notesInput.setText(state.notes)
      MaintenanceViewModel.Status.ERROR_PREVIOUS_NOTES -> {
        copyNotesCheckbox.isChecked = false
        showToast(R.string.no_notes_found)
      }
    }
  }

  private fun showMaintenanceDetails(carWork: CarWork) {
    setTitle(carWork.name)
    val adapter = nameSpinner.adapter as ArrayAdapter<String>
    val positionOfName = adapter.getPosition(carWork.name)

    if (positionOfName != -1) { // found name in adapter
      nameSpinner.setSelection(positionOfName)
    } else {
      // set to 'other' and show input field
      nameSpinner.setSelection(adapter.count - 1)
      nameInput.setText(carWork.name)
      nameInput.setSelection(carWork.name.length)
    }

    aftermarketModificationsCheckbox.isChecked = carWork.type == CarWork.Type.MODIFICATION
    dateInput.setText(carWork.date.formatted())
    costInput.setText(carWork.cost.formatMoney())
    milesInput.setText(carWork.miles.toString())
    notesInput.setText(carWork.notes)
  }

  private fun validForm(): Boolean {
    return (validName()
            and FormValidator.validateRequired(this, dateLayout)
            and FormValidator.validateRequired(this, milesLayout))
  }

  private fun validName(): Boolean {
    return (!nameLayout.visible && nameSpinner.selectedItem.toString() != getString(R.string.other))
            || FormValidator.validateRequired(this, nameLayout)
  }

  private fun saveCarWork() {
    maintenanceViewModel.carId?.let { carId ->
      val name = getJobName()
      val date = dateInput.text.toString().toDateOrNull() ?: LocalDate.now()
      val miles = milesInput.text.toString().toInt()
      val cost = costInput.text.toString().removePrefix("$").toDoubleOrNull()
      val notes = notesInput.text.toString()
      val type = if (aftermarketModificationsCheckbox.isChecked) CarWork.Type.MODIFICATION else CarWork.Type.MAINTENANCE
      val addReminder = addReminderCheckbox.isChecked
      // null carId should auto generate an ID in Room
      val newCarWork = CarWork(
          maintenanceViewModel.workId, carId,
          name, type, date, cost, miles, notes)

      maintenanceViewModel.saveWork(newCarWork, addReminder)
    } ?: showToast(getString(R.string.error_occurred))
  }

  private fun getJobName(): String {
    val selectedName = nameSpinner.selectedItem.toString()

    return if (!nameSpinner.visible || selectedName == getString(R.string.other)) {
      nameInput.text.toString()
    } else {
      selectedName
    }
  }

  private fun showManualPreferenceInput(carWork: CarWork) {
    PreferenceInputDialogFragment.show(this, carWork)
  }

  override fun onPreferenceSet(carWork: CarWork, miles: Int, months: Int) {
    maintenanceViewModel.addReminderFromManualPref(carWork, miles, months)
  }

  companion object {
    private const val CAR_ID = "carId"
    private const val MAINTENANCE_ID = "maintenanceId"
    private const val ID_DEFAULT = -1L

    fun newIntent(context: Context, carId: Long, carWorkId: Long? = null): Intent {
      return Intent(context, CarWorkDetailsActivity::class.java).apply {
        putExtra(MAINTENANCE_ID, carWorkId)
        putExtra(CAR_ID, carId)
      }
    }
  }
}