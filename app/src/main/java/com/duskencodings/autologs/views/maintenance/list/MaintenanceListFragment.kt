package com.duskencodings.autologs.views.maintenance.list

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_car_work_list.*
import com.duskencodings.autologs.R
import com.duskencodings.autologs.base.BaseFragment
import com.duskencodings.autologs.models.CAR_ID
import com.duskencodings.autologs.models.CAR_ID_DEFAULT
import com.duskencodings.autologs.models.CarWork
import com.duskencodings.autologs.models.Resource
import com.duskencodings.autologs.utils.visible
import com.duskencodings.autologs.views.maintenance.MaintenanceViewModel
import com.duskencodings.autologs.views.maintenance.details.CarWorkDetailsActivity
import java.lang.NullPointerException

class MaintenanceListFragment : BaseFragment() {

  private lateinit var maintenanceViewModel: MaintenanceViewModel

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.fragment_car_work_list, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val id = arguments?.getLong(CAR_ID, CAR_ID_DEFAULT)

    maintenanceViewModel = getViewModel(this)

    if (CAR_ID_DEFAULT != id) {
      maintenanceViewModel.carId = id
    }

    initUi()

    maintenanceViewModel.listState.observeForever {
      it?.let {
        updateFromState(it)
      }
    }
    maintenanceViewModel.getLiveCarWorkRecords()
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
        onError(it)
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

  private fun navigateToMaintenanceDetails(carWorkId: Long? = null) {
    context?.let { context ->
      maintenanceViewModel.carId?.let { carId ->
        startActivity(CarWorkDetailsActivity.newIntent(context, carId, carWorkId))
      } ?: onError(NullPointerException("${javaClass.simpleName}.navigateToMaintenanceDetails -> null carId"))
    }
  }

  private fun showMaintenanceRecords(maintenanceRecords: List<CarWork>) {
    context?.let { context ->
      val adapter = MaintenanceAdapter(context, maintenanceRecords) { maintenance ->
        navigateToMaintenanceDetails(maintenance.id)
      }

      carWorkList.adapter = adapter
      carWorkList.layoutManager = LinearLayoutManager(context)
      carWorkList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
          super.onScrollStateChanged(recyclerView, newState)
          add.visible = newState == RecyclerView.SCROLL_STATE_IDLE
        }
      })
      carWorkList.visible = true
      emptyResults.visible = false
    }
  }

  private fun showNoResults() {
    carWorkList.visible = false
    emptyResults.visible = true
  }

  companion object {

    fun newInstance(carId: Long? = null): MaintenanceListFragment {
      return  MaintenanceListFragment().apply {
        arguments = Bundle().apply {
          carId?.let { id -> putLong(CAR_ID, id) }
        }
      }
    }
  }
}