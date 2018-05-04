package sample.thowes.autoservice.views.maintenance.list

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_car_work_list.*
import sample.thowes.autoservice.R
import sample.thowes.autoservice.base.BaseFragment
import sample.thowes.autoservice.models.CarWork
import sample.thowes.autoservice.views.cars.details.CarDetailsActivity
import sample.thowes.autoservice.views.maintenance.details.CarWorkDetailsActivity
import sample.thowes.autoservice.views.maintenance.MaintenanceViewModel

class MaintenanceListFragment : BaseFragment() {

  private lateinit var maintenanceViewModel: MaintenanceViewModel
  private var carId: Int? = null
  private var type: Int? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.fragment_car_work_list, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val id = arguments?.getInt(CarDetailsActivity.CAR_ID, CarDetailsActivity.CAR_ID_DEFAULT)
    type = arguments?.getInt(TYPE, CarWork.Type.MAINTENANCE.value) ?: CarWork.Type.MAINTENANCE.value

    if (CarDetailsActivity.CAR_ID_DEFAULT != id) {
      carId = id
    }

    initUi()

    maintenanceViewModel = ViewModelProviders.of(this).get(MaintenanceViewModel::class.java)
    maintenanceViewModel.state.observe(this, Observer {
      it?.let {
        updateFromState(it)
      }
    })
    maintenanceViewModel.getLiveCarWorkRecords(carId, type)
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
        showLoading(false)
      }
      MaintenanceViewModel.MaintenanceStatus.ERROR -> state.error?.let {
        showLoading(false)
        showToast(it.localizedMessage)
      }
      MaintenanceViewModel.MaintenanceStatus.MAINTENANCE_LIST_RETRIEVED -> {
        state.maintenance?.let {
          showMaintenanceRecords(it)
        }
        showLoading(false)
      }
      MaintenanceViewModel.MaintenanceStatus.MAINTENANCE_RETRIEVED -> {
        // not implementing here
      }
    }
  }

  private fun navigateToMaintenanceDetails(carWorkId: Int? = null) {
    context?.let { context ->
      carId?.let { carId ->
        startActivity(CarWorkDetailsActivity.newIntent(context, carId, carWorkId, type!!))
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
    }
  }

  private fun showNoResults() {
    carWorkList.visibility = View.GONE
    val textRes = when (type) {
      CarWork.Type.MAINTENANCE.value -> R.string.maintenance
      CarWork.Type.MODIFICATION.value -> R.string.modifications
      else -> R.string.maintenance
    }
    val text = getString(textRes).toLowerCase()
    emptyResults.text = getString(R.string.no_car_work, text)
    emptyResults.visibility = View.VISIBLE
  }

  companion object {
    private const val TYPE = "type"

    fun newInstance(carId: Int? = null, type: Int = CarWork.Type.MAINTENANCE.value): MaintenanceListFragment {
      val fragment = MaintenanceListFragment()
      val args = Bundle()
      args.putInt(TYPE, type)

      carId?.let { id ->
        args.putInt(CarDetailsActivity.CAR_ID, id)
      }

      fragment.arguments = args
      return fragment
    }
  }
}