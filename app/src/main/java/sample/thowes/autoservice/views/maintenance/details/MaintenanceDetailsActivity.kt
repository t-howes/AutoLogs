package sample.thowes.autoservice.views.maintenance.details

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import kotlinx.android.synthetic.main.activity_maintenance_details.*
import sample.thowes.autoservice.R
import sample.thowes.autoservice.base.BaseActivity
import sample.thowes.autoservice.extensions.formatMoney
import sample.thowes.autoservice.extensions.showToast
import sample.thowes.autoservice.models.CarWork
import sample.thowes.autoservice.views.maintenance.MaintenanceViewModel
import android.widget.ArrayAdapter



class MaintenanceDetailsActivity : BaseActivity() {

  private lateinit var maintenanceViewModel: MaintenanceViewModel
  private var id: Int? = null
  private var type: Int? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_maintenance_details)

    val id = intent.extras?.getInt(MAINTENANCE_ID, ID_DEFAULT)
    val type = intent.extras?.getInt(TYPE, CarWork.Type.OTHER.value)

    if (ID_DEFAULT != id) {
      this.id = id
    }
    if (CarWork.Type.OTHER.value != type) {
      this.type = type
    }

    initUi()

    maintenanceViewModel = ViewModelProviders.of(this).get(MaintenanceViewModel::class.java)
    maintenanceViewModel.state.observe(this, Observer {
      it?.let {
        updateFromState(it)
      }
    })

    maintenanceViewModel.getMaintenance(id)
  }

  private fun initUi() {
    setDisplayHomeAsUpEnabled()

    if (id == null) {
      val textRes = when (type) {
        CarWork.Type.MAINTENANCE.value -> R.string.maintenance
        CarWork.Type.MODIFICATION.value -> R.string.modifications
        else -> R.string.maintenance
      }
      val text = getString(textRes)
      setTitle(getString(R.string.add_placeholder, text))
    } else {
      submit.text = getString(R.string.save)
    }

    setupNameSpinner()
    submit.setOnClickListener {
      if (validForm()) {
        saveCarWork()
      }
    }
  }

  private fun setupNameSpinner() {
    val adapter = ArrayAdapter.createFromResource(this, R.array.maintenance_names, android.R.layout.simple_spinner_item)
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    nameSpinner.adapter = adapter
    nameSpinner.onItemSelectedListener = object : OnItemSelectedListener {
      override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
         nameLayout.visibility = if (adapter.getItem(position) == getString(R.string.other)) View.VISIBLE else View.GONE
      }

      override fun onNothingSelected(parent: AdapterView<*>?) {

      }

    }
  }

  private fun updateFromState(state: MaintenanceViewModel.MaintenanceListState) {
    when (state.status) {
      MaintenanceViewModel.MaintenanceStatus.IDLE -> showLoading(false)
      MaintenanceViewModel.MaintenanceStatus.LOADING -> showLoading()
      MaintenanceViewModel.MaintenanceStatus.NO_MAINTENANCE -> {
        // not implementing
      }
      MaintenanceViewModel.MaintenanceStatus.ERROR -> state.error?.let {
        showLoading(false)
        showToast(it.localizedMessage)
      }
      MaintenanceViewModel.MaintenanceStatus.MAINTENANCE_LIST_RETRIEVED -> {
        // not implementing
      }
      MaintenanceViewModel.MaintenanceStatus.MAINTENANCE_RETRIEVED -> {
        state.maintenance?.let {
          showMaintenanceDetails(it.first())
        }
        showLoading(false)
      }
    }
  }

  private fun showMaintenanceDetails(carWork: CarWork) {
    setTitle(carWork.name)
    nameInput.setText(carWork.name)
    dateInput.text = carWork.date
    costInput.setText(carWork.cost.formatMoney())
    milesInput.setText(carWork.odometerReading.toString())
    notesInput.setText(carWork.notes)
  }

  private fun validForm(): Boolean {
    return true
  }

  private fun saveCarWork() {

  }

  companion object {
    private const val MAINTENANCE_ID = "maintenanceId"
    private const val ID_DEFAULT = -1
    private const val TYPE = "type"

    fun newIntent(context: Context, id: Int? = null, type: Int? = null): Intent {
      val intent = Intent(context, MaintenanceDetailsActivity::class.java)
      intent.putExtra(MAINTENANCE_ID, id)
      type?.let { intent.putExtra(TYPE, it) }
      return intent
    }
  }
}