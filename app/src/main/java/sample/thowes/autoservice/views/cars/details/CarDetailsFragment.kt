package sample.thowes.autoservice.views.cars.details

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import sample.thowes.autoservice.R
import sample.thowes.autoservice.base.BaseFragment
import sample.thowes.autoservice.extensions.showToast
import sample.thowes.autoservice.models.*
import sample.thowes.autoservice.views.maintenance.MaintenanceViewModel

class CarDetailsFragment : BaseFragment() {

  private lateinit var carViewModel: CarViewModel
  private lateinit var maintenanceViewModel: MaintenanceViewModel
  private var carId: Int? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.fragment_car_details, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val id = arguments?.getInt(CAR_ID, CAR_ID_DEFAULT) ?: CAR_ID_DEFAULT

    if (CAR_ID_DEFAULT != id) {
      carId = id
    }

    initUi()

    carViewModel = ViewModelProviders.of(this).get(CarViewModel::class.java)
    maintenanceViewModel = ViewModelProviders.of(this).get(MaintenanceViewModel::class.java)

    carViewModel.detailsState.observe(this, Observer {
      it?.let {
        updateCarState(it)
      }
    })

    maintenanceViewModel.listState.observe(this, Observer {
      it?.let {
        updateMaintenanceState(it)
      }
    })

    carViewModel.getCar(carId)
    maintenanceViewModel.getLiveCarWorkRecords(carId)
  }

  private fun initUi() {
    // TODO
  }

  private fun updateCarState(state: Resource<Car>) {
    when (state.status) {
      Resource.Status.IDLE -> showLoading(false)
      Resource.Status.LOADING -> showLoading()
      Resource.Status.ERROR -> {
        state.error?.let {
          context.showToast(it.localizedMessage)
        }
      }
      Resource.Status.SUCCESS -> {
        state.data?.let {
          showCarDetails(it)
        }
      }
    }
  }

  private fun updateMaintenanceState(state: Resource<List<CarWork>>) {
    when (state.status) {
      Resource.Status.IDLE -> showLoading(false)
      Resource.Status.LOADING -> showLoading()
      Resource.Status.ERROR -> {
        state.error?.let {
          context.showToast(it.localizedMessage)
        }
      }
      Resource.Status.SUCCESS -> {
        state.data?.let {
          // TODO: do stuff with maintenance (stats and whatnot)
        }
      }
    }
  }

  private fun showCarDetails(car: Car) {
    setTitle(car.name ?: car.yearMakeModel())
    // TODO
  }

  companion object {
    fun newInstance(carId: Int? = null): CarDetailsFragment {
      val fragment = CarDetailsFragment()
      val args = Bundle()

      carId?.let { id ->
        args.putInt(CAR_ID, id)
      }

      fragment.arguments = args
      return fragment
    }
  }
}