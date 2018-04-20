package sample.thowes.autoservice.views.carList

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.activity_car_list.*
import sample.thowes.autoservice.R
import sample.thowes.autoservice.base.BaseActivity
import sample.thowes.autoservice.extensions.showToast
import sample.thowes.autoservice.models.Car
import sample.thowes.autoservice.views.carDetails.CarDetailsActivity
import sample.thowes.autoservice.views.carList.CarsViewModel.CarsStatus.*

class CarListActivity : BaseActivity(), CarClickListener {

  private lateinit var carsViewModel: CarsViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_car_list)

    carsViewModel = ViewModelProviders.of(this).get(CarsViewModel::class.java)
    carsViewModel.state.observe(this, Observer<CarsViewModel.CarResultsState> {
      it?.let { state ->
        updateFromStatus(state)
      }
    })
    carsViewModel.getCars()

    add.setOnClickListener {
      navigateToCarDetails()
    }
  }

  private fun updateFromStatus(state: CarsViewModel.CarResultsState) {
    when (state.status) {
      IDLE -> showLoading(false)
      LOADING -> showLoading()
      EMPTY -> emptyResults.visibility = View.VISIBLE
      ERROR -> {
        state.error?.let {
          showToast(it.localizedMessage)
        }
      }
      SUCCESS -> {
        state.cars?.let { cars ->
          showCars(cars)
        }
      }
    }

    showLoading(false)
  }

  private fun showCars(cars: List<Car>) {
    val adapter = CarAdapter(this, cars)
    carsList.adapter = adapter
    carsList.layoutManager = LinearLayoutManager(this)
    emptyResults.visibility = View.GONE
  }

  private fun navigateToCarDetails(id: Int? = null) {
    startActivity(CarDetailsActivity.newIntent(this, id))
  }

  override fun onCarClicked(car: Car) {
    navigateToCarDetails(car.id)
  }
}
