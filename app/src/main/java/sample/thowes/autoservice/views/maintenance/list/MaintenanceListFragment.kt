package sample.thowes.autoservice.views.maintenance.list

import androidx.lifecycle.Observer
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_aggregate_data.*
import kotlinx.android.synthetic.main.fragment_car_work_list.*
import sample.thowes.autoservice.R
import sample.thowes.autoservice.base.BaseFragment
import sample.thowes.autoservice.models.CAR_ID
import sample.thowes.autoservice.models.CAR_ID_DEFAULT
import sample.thowes.autoservice.models.CarWork
import sample.thowes.autoservice.models.Resource
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
    val id = arguments?.getInt(CAR_ID, CAR_ID_DEFAULT)

    if (CAR_ID_DEFAULT != id) {
      carId = id
    }

    initUi()

    maintenanceViewModel = getViewModel(this)
    maintenanceViewModel.listState.observe(this, Observer {
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

  private fun updateFromState(state: Resource<List<CarWork>>) {
    when (state.status) {
      Resource.Status.IDLE -> showLoading(false)
      Resource.Status.LOADING -> showLoading()
      Resource.Status.ERROR -> state.error?.let {
        showToast(it.localizedMessage)
      }
      Resource.Status.SUCCESS -> {
        state.data?.let {
          if (it.isEmpty()) {
            showNoResults()
          } else {
            showMaintenanceRecords(it)
          }
        } ?: showNoResults()
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
        args.putInt(CAR_ID, id)
      }

      fragment.arguments = args
      return fragment
    }
  }
}