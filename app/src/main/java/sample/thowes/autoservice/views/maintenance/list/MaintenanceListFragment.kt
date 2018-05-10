package sample.thowes.autoservice.views.maintenance.list

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_aggregate_data.*
import kotlinx.android.synthetic.main.fragment_car_work_list.*
import sample.thowes.autoservice.R
import sample.thowes.autoservice.base.BaseFragment
import sample.thowes.autoservice.models.CarWork
import sample.thowes.autoservice.views.cars.details.AddCarActivity
import sample.thowes.autoservice.views.maintenance.MaintenanceViewModel
import sample.thowes.autoservice.views.maintenance.details.CarWorkDetailsActivity

class MaintenanceListFragment : BaseFragment() {

  private lateinit var maintenanceViewModel: MaintenanceViewModel
  private var carId: Int? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.fragment_car_work_list, container, false)
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

  private fun updateFromState(state: MaintenanceViewModel.MaintenanceState) {
    when (state.status) {
      MaintenanceViewModel.MaintenanceStatus.IDLE -> showLoading(false)
      MaintenanceViewModel.MaintenanceStatus.LOADING -> showLoading()
      MaintenanceViewModel.MaintenanceStatus.NO_MAINTENANCE -> {
        showNoResults()
      }
      MaintenanceViewModel.MaintenanceStatus.ERROR -> state.error?.let {
        showToast(it.localizedMessage)
      }
      MaintenanceViewModel.MaintenanceStatus.MAINTENANCE_LIST_RETRIEVED -> {
        state.maintenance?.let {
          showMaintenanceRecords(it)
        }
      }
      MaintenanceViewModel.MaintenanceStatus.MAINTENANCE_RETRIEVED -> {
        // not implementing here
      }
      MaintenanceViewModel.MaintenanceStatus.SUBMIT -> {
        // not implementing here
      }
    }
  }

  private fun navigateToMaintenanceDetails(carWorkId: Int? = null) {
    context?.let { context ->
      carId?.let { carId ->
        startActivity(CarWorkDetailsActivity.newIntent(context, carId, carWorkId))
      } ?: showToast(getString(R.string.error_occurred))
    }
  }

  private fun showMaintenanceRecords(maintenanceRecords: List<CarWork>) {
    context?.let { context ->
      val adapter = MaintenanceAdapter(context, maintenanceRecords)
      adapter.setOnMaintenanceClickedListener({ maintenance ->
        navigateToMaintenanceDetails(maintenance.id)
      })

      carWorkList.adapter = adapter
      carWorkList.layoutManager = LinearLayoutManager(context)
      carWorkList.visibility = View.VISIBLE
      emptyResults.visibility = View.GONE

      getAggregateData()
    }
  }

  private fun getAggregateData() {
    //TODO: aggregate data
    aggregateData.visibility = View.VISIBLE
  }

  private fun showNoResults() {
    carWorkList.visibility = View.GONE
    emptyResults.visibility = View.VISIBLE
  }

  companion object {

    fun newInstance(carId: Int? = null): MaintenanceListFragment {
      val fragment = MaintenanceListFragment()
      val args = Bundle()

      carId?.let { id ->
        args.putInt(AddCarActivity.CAR_ID, id)
      }

      fragment.arguments = args
      return fragment
    }
  }
}