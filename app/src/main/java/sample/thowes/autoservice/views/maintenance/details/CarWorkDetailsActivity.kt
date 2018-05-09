package sample.thowes.autoservice.views.maintenance.details

import android.app.DatePickerDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_maintenance_details.*
import sample.thowes.autoservice.R
import sample.thowes.autoservice.base.BaseActivity
import sample.thowes.autoservice.extensions.formatMoney
import sample.thowes.autoservice.extensions.showToast
import sample.thowes.autoservice.extensions.simple
import sample.thowes.autoservice.models.CarWork
import sample.thowes.autoservice.validation.FormValidator
import sample.thowes.autoservice.views.maintenance.MaintenanceViewModel
import java.util.*


class CarWorkDetailsActivity : BaseActivity() {

  private lateinit var maintenanceViewModel: MaintenanceViewModel
  private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
  private val calendar = Calendar.getInstance()
  private var carId: Int? = null
  private var maintenanceId: Int? = null
  private var type: Int = CarWork.Type.MAINTENANCE.value

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_maintenance_details)
    val carId = intent.extras?.getInt(CAR_ID, -1)
    val id = intent.extras?.getInt(MAINTENANCE_ID, ID_DEFAULT)
    type = intent.extras?.getInt(TYPE, CarWork.Type.MAINTENANCE.value) ?: CarWork.Type.MAINTENANCE.value

    if (carId != ID_DEFAULT) {
      this.carId = carId
    }
    if (id != ID_DEFAULT) {
      this.maintenanceId = id
    }

    initUi()

    maintenanceViewModel = ViewModelProviders.of(this).get(MaintenanceViewModel::class.java)
    maintenanceViewModel.state.observe(this, Observer {
      it?.let {
        updateFromState(it)
      }
    })

    maintenanceViewModel.getMaintenance(maintenanceId)
  }

  private fun initUi() {
    setDisplayHomeAsUpEnabled()

    if (maintenanceId == null) {
      val titleRes = when (CarWork.Type.from(type)) {
        CarWork.Type.MAINTENANCE -> {
          setupNameSpinner()
          R.string.maintenance
        }
        CarWork.Type.MODIFICATION -> R.string.modification
      }

      val text = getString(titleRes)
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
        nameLayout.visibility = if (isOther) View.VISIBLE else View.GONE

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

  private fun updateFromState(state: MaintenanceViewModel.MaintenanceState) {
    when (state.status) {
      MaintenanceViewModel.MaintenanceStatus.IDLE -> showLoading(false)
      MaintenanceViewModel.MaintenanceStatus.LOADING -> showLoading()
      MaintenanceViewModel.MaintenanceStatus.NO_MAINTENANCE -> {
        // not implementing
      }
      MaintenanceViewModel.MaintenanceStatus.ERROR -> state.error?.let {
        showToast(it.localizedMessage)
      }
      MaintenanceViewModel.MaintenanceStatus.MAINTENANCE_LIST_RETRIEVED -> {
        // not implementing
      }
      MaintenanceViewModel.MaintenanceStatus.MAINTENANCE_RETRIEVED -> {
        state.maintenance?.let {
          showMaintenanceDetails(it.first())
        }
      }
      MaintenanceViewModel.MaintenanceStatus.SUBMIT -> finish()
    }
  }

  private fun showMaintenanceDetails(carWork: CarWork) {
    setTitle(carWork.name)
    val adapter = nameSpinner.adapter as? ArrayAdapter<String>
    val positionOfName = adapter?.getPosition(carWork.name) ?: -1

    if (positionOfName != -1) {
      nameSpinner.setSelection(positionOfName)
    } else {
      nameSpinner.visibility = View.GONE
      nameLayout.visibility = View.VISIBLE
      nameInput.setText(carWork.name)
    }

    dateInput.setText(carWork.date)
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
    carId?.let { carId ->
      val selectedName = nameSpinner.selectedItem.toString()

      val name = if (nameSpinner.visibility == View.GONE || selectedName == getString(R.string.other)) {
        nameInput.text.toString()
      } else {
        selectedName
      }
      val date = dateInput.text.toString()
      val miles = milesInput.text.toString().toInt()
      val cost = costInput.text.toString().toDoubleOrNull()
      val notes = notesInput.text.toString()
      // null carId should auto generate an ID in Room
      val newCarWork = CarWork(
          maintenanceId, carId,
          type,
          name, date, cost, miles, notes)

      maintenanceViewModel.updateCar(newCarWork)
    } ?: showToast(getString(R.string.error_occurred))
  }

  companion object {
    private const val CAR_ID = "carId"
    private const val MAINTENANCE_ID = "maintenanceId"
    private const val ID_DEFAULT = -1
    private const val TYPE = "type"

    fun newIntent(context: Context, carId: Int, carWorkId: Int? = null): Intent {
      val intent = Intent(context, CarWorkDetailsActivity::class.java)
      intent.putExtra(MAINTENANCE_ID, carWorkId)
      intent.putExtra(TYPE, carWorkType)
      intent.putExtra(CAR_ID, carId)
      return intent
    }
  }
}