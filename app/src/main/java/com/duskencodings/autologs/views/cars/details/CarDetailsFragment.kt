package com.duskencodings.autologs.views.cars.details

import androidx.lifecycle.Observer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.duskencodings.autologs.R
import com.duskencodings.autologs.base.BaseFragment
import com.duskencodings.autologs.extensions.showToast
import com.duskencodings.autologs.models.*
import com.duskencodings.autologs.views.maintenance.MaintenanceViewModel
import com.duskencodings.autologs.views.maintenance.upcoming.UpcomingMaintenanceAdapter
import kotlinx.android.synthetic.main.fragment_car_details.*

class CarDetailsFragment : BaseFragment() {

  private lateinit var carViewModel: CarViewModel
  private var carId: Int = CAR_ID_DEFAULT // will have a valid ID passed in args

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.fragment_car_details, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    carId = arguments?.getInt(CAR_ID, CAR_ID_DEFAULT) ?: CAR_ID_DEFAULT

    initUi()

    carViewModel = getViewModel(this)

    carViewModel.detailsState.observe(this, Observer {
      it?.let { state ->
        updateCarState(state)
      }
    })

    carViewModel.getCar(carId)
  }

  private fun initUi() {
    upcoming_maintenance.apply {
      layoutManager = LinearLayoutManager(context)
      adapter = UpcomingMaintenanceAdapter(context) {
        context.showToast("TODO: maintenance clicked")
      }
    }
  }

  private fun updateCarState(state: Resource<Car>) {
    when (state.status) {
      Resource.Status.IDLE -> showLoading(false)
      Resource.Status.LOADING -> showLoading()
      Resource.Status.ERROR -> {
        state.error?.let {
          onError(it)
        }
      }
      Resource.Status.SUCCESS -> {
        state.data?.let {
          showCarDetails(it)
        }
      }
    }
  }

  private fun showCarDetails(car: Car) {
    val name = car.name
    setTitle(if (name.isNullOrBlank()) car.yearMakeModel() else name)
    // TODO
//    Picasso.get().load("file: some file")
//        .transform(Crop())
//        .into(someImageView)
  }

  companion object {
    fun newInstance(carId: Int): CarDetailsFragment {
      return CarDetailsFragment().apply {
        arguments = Bundle().apply {
          putInt(CAR_ID, carId)
        }
      }
    }
  }
}