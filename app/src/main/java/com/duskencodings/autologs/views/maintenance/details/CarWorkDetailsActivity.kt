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
import com.duskencodings.autologs.utils.formatMoney
import com.duskencodings.autologs.utils.showToast
import com.duskencodings.autologs.utils.simple
import com.duskencodings.autologs.models.CarWork
import com.duskencodings.autologs.models.Resource
import com.duskencodings.autologs.utils.toDateOrNull
import com.duskencodings.autologs.validation.FormValidator
import com.duskencodings.autologs.views.maintenance.MaintenanceViewModel
import java.time.LocalDate
import java.util.*


class CarWorkDetailsActivity : BaseActivity() {

  private lateinit var maintenanceViewModel: MaintenanceViewModel
  private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
  private val calendar = Calendar.getInstance()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_maintenance_details)
    val carId = intent.extras?.getInt(CAR_ID, -1)
    val id = intent.extras?.getInt(MAINTENANCE_ID, ID_DEFAULT)

    maintenanceViewModel = getViewModel()

    if (carId != ID_DEFAULT) {
      maintenanceViewModel.carId = carId
    }
    if (id != ID_DEFAULT) {
      maintenanceViewModel.maintenanceId = id
    }

    initUi()

    maintenanceViewModel.detailsState.observe(this, Observer {
      it?.let {
        updateDetailsState(it)
      }
    })

    maintenanceViewModel.submitState.observe(this, Observer {
      it?.let {
        updateSubmitState(it)
      }
    })

    maintenanceViewModel.getMaintenance()
  }

  private fun initUi() {
    setDisplayHomeAsUpEnabled()
    setupNameSpinner()

    if (maintenanceViewModel.maintenanceId == null) {
      val text = getString(R.string.service)
      setTitle(getString(R.string.add_placeholder, text))
      dateInput.setText(Calendar.getInstance().simple())
    } else {
      submit.text = getString(R.string.save)
    }

    setupListeners()
    setupLiveValidations()
  }

  private fun setupNameSpinner() {
    val adapter = ArrayAdapter.createFromResource(this, R.array.maintenance_names, android.R.layout.simple_spinner_item)
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    nameSpinner.adapter = adapter
    nameSpinner.onItemSelectedListener = object : OnItemSelectedListener {
      override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val isOther = adapter.getItem(position) == getString(R.string.other)
        val extraInputVisibility = if (isOther) View.VISIBLE else View.GONE
        nameLayout.visibility = extraInputVisibility
        aftermarketModificationsCheckbox.visibility = extraInputVisibility

        if (isOther) {
          nameLayout.requestFocus()
        }
      }

      override fun onNothingSelected(parent: AdapterView<*>?) { }
    }
  }

  private fun setupListeners() {
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
    dateInput.setText(calendar.simple())
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
        calendar.get(Calendar.DAY_OF_MONTH)).show()
  }

  private fun updateDetailsState(state: Resource<CarWork>) {
    when (state.status) {
      Resource.Status.IDLE -> showLoading(false)
      Resource.Status.LOADING -> showLoading()
      Resource.Status.ERROR -> state.error?.let {
        showToast(it.localizedMessage)
      }
      Resource.Status.SUCCESS -> {
        state.data?.let {
          showMaintenanceDetails(it)
        }
      }
    }
  }

  private fun updateSubmitState(state: Resource<CarWork>) {
    when (state.status) {
      Resource.Status.IDLE -> showLoading(false)
      Resource.Status.LOADING -> showLoading()
      Resource.Status.ERROR -> state.error?.let {
        showToast(it.localizedMessage)
      }
      Resource.Status.SUCCESS -> {
        finish()
      }
    }
  }

  private fun showMaintenanceDetails(carWork: CarWork) {
    setTitle(carWork.name)
    val adapter = nameSpinner.adapter as? ArrayAdapter<String>
    val positionOfName = adapter?.getPosition(carWork.name) ?: -1

    if (positionOfName != -1) { // found name in adapter
      nameSpinner.setSelection(positionOfName)
    } else {
      // set to 'other' and show input field
      nameSpinner.setSelection(adapter?.count ?: 0)
      nameInput.setText(carWork.name)
    }

    dateInput.setText(carWork.date.toString())
    costInput.setText(carWork.cost.formatMoney())
    milesInput.setText(carWork.odometerReading.toString())
    notesInput.setText(carWork.notes)
  }

  private fun validForm(): Boolean {
    return (validName()
            and FormValidator.validateRequired(this, dateLayout)
            and FormValidator.validateRequired(this, milesLayout))
  }

  private fun validName(): Boolean {
    return (nameLayout.visibility == View.GONE && nameSpinner.selectedItem.toString() != getString(R.string.other))
            || FormValidator.validateRequired(this, nameLayout)
  }

  private fun saveCarWork() {
    maintenanceViewModel.carId?.let { carId ->
      val selectedName = nameSpinner.selectedItem.toString()

      val name = if (nameSpinner.visibility == View.GONE || selectedName == getString(R.string.other)) {
        nameInput.text.toString()
      } else {
        selectedName
      }
      val date = dateInput.text.toString().toDateOrNull() ?: LocalDate.now()
      val miles = milesInput.text.toString().toInt()
      val cost = costInput.text.toString().toDoubleOrNull()
      val notes = notesInput.text.toString()
      val type = getType(name)
      // null carId should auto generate an ID in Room
      val newCarWork = CarWork(
          maintenanceViewModel.maintenanceId, carId,
          name, type, date, cost, miles, notes)

      maintenanceViewModel.saveWork(newCarWork)
    } ?: showToast(getString(R.string.error_occurred))
  }

  /**
   * TODO: Consider doing a name mapping later.
   */
  private fun getType(name: String): CarWork.Type {
    return if (aftermarketModificationsCheckbox.isChecked) CarWork.Type.MODIFICATION else CarWork.Type.MAINTENANCE
  }

  companion object {
    private const val CAR_ID = "carId"
    private const val MAINTENANCE_ID = "maintenanceId"
    private const val ID_DEFAULT = -1

    fun newIntent(context: Context, carId: Int, carWorkId: Int? = null): Intent {
      return Intent(context, CarWorkDetailsActivity::class.java).apply {
        putExtra(MAINTENANCE_ID, carWorkId)
        putExtra(CAR_ID, carId)
      }
    }
  }
}