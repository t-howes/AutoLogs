package sample.thowes.autoservice.views.maintenance.fuel

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_car_work_list.*
import sample.thowes.autoservice.R
import sample.thowes.autoservice.base.BaseFragment
import sample.thowes.autoservice.views.cars.details.AddCarActivity
import sample.thowes.autoservice.views.maintenance.MaintenanceViewModel


class FuelListActivity : BaseFragment() {

  private lateinit var fuelViewModel: FuelViewModel
  private var carId: Int? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.fragment_fuel_list, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val id = arguments?.getInt(AddCarActivity.CAR_ID, AddCarActivity.CAR_ID_DEFAULT)

    if (AddCarActivity.CAR_ID_DEFAULT != id) {
      carId = id
    }

    initUi()

    maintenanceViewModel = ViewModelProviders.of(this).get(MaintenanceViewModel::class.java)
    maintenanceViewModel.state.observe(this, Observer {
      it?.let {
        updateFromState(it)
      }
    })
    maintenanceViewModel.getLiveCarWorkRecords(carId)
  }

  private fun initUi() {
    add.setOnClickListener {
      navigateToMaintenanceDetails()
    }
  }

}