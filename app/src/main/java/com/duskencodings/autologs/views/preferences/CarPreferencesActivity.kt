package com.duskencodings.autologs.views.preferences

import androidx.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.ArrayRes
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_car_preferences.*
import com.duskencodings.autologs.R
import com.duskencodings.autologs.base.BaseActivity
import com.duskencodings.autologs.extensions.showToast
import com.duskencodings.autologs.models.*
import com.duskencodings.autologs.notifications.ReminderService

class CarPreferencesActivity : BaseActivity() {

  private lateinit var preferencesViewModel: PreferencesViewModel
  private var carId: Int? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_car_preferences)
    setDisplayHomeAsUpEnabled()
    ReminderService.scheduleNotification(this, ReminderService.getNotification(this, "tah-dah"), 10)
    val id = intent?.extras?.getInt(CAR_ID, CAR_ID_DEFAULT) ?: CAR_ID_DEFAULT

    if (CAR_ID_DEFAULT != id) {
      carId = id
    }

    initUi()

    preferencesViewModel = getViewModel()
    preferencesViewModel.prefsState.observe(this, Observer {
      it?.let {
        updateFromState(it)
      }
    })

    preferencesViewModel.getLivePreferences(carId)
  }

  private fun initUi() {
    oilLayout.findViewById<TextView>(R.id.header).text = getString(R.string.oil)
    setupSpinnerAdapter(oilLayout.findViewById(R.id.spinner), R.array.oil_preferences)
    airFilterLayout.findViewById<TextView>(R.id.header).text = getString(R.string.air_filter)
    setupSpinnerAdapter(airFilterLayout.findViewById(R.id.spinner), R.array.air_filter_preferences)
  }

  private fun setupSpinnerAdapter(spinner: Spinner, @ArrayRes arrayRes: Int) {
    val adapter = ArrayAdapter.createFromResource(this, arrayRes, android.R.layout.simple_spinner_item)
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    spinner.adapter = adapter
  }

  private fun updateFromState(state: Resource<List<Preference>>) {
    when (state.status) {
      Resource.Status.IDLE -> showLoading(false)
      Resource.Status.LOADING -> showLoading()
      Resource.Status.ERROR -> {
        state.error?.let {
          showToast(it.localizedMessage)
        }
      }
      Resource.Status.SUCCESS -> {
        state.data?.let {
          // TODO
        }
      }
    }
  }

  companion object {

    fun newIntent(context: Context, carId: Int? = null): Intent {
      val intent = Intent(context, CarPreferencesActivity::class.java)

      carId?.let { id ->
        intent.putExtra(CAR_ID, id)
      }

      return intent
    }
  }
}