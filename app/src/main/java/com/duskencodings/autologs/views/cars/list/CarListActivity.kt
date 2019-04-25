package com.duskencodings.autologs.views.cars.list

import android.content.DialogInterface
import androidx.lifecycle.Observer
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_car_list.*
import com.duskencodings.autologs.R
import com.duskencodings.autologs.base.BaseActivity
import com.duskencodings.autologs.extensions.showToast
import com.duskencodings.autologs.models.Car
import com.duskencodings.autologs.models.Resource
import com.duskencodings.autologs.views.cars.details.AddCarActivity
import com.duskencodings.autologs.views.cars.details.CarDetailsActivity
import com.duskencodings.autologs.views.preferences.CarPreferencesActivity


class CarListActivity : BaseActivity(), CarClickListener {

  private lateinit var carsViewModel: CarsViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_car_list)

    carsViewModel = getViewModel()
    carsViewModel.state.observe(this, Observer<Resource<List<Car>>> {
      it?.let { state ->
        updateFromStatus(state)
      }
    })

    initUi()
    carsViewModel.getCars()
  }

  private fun initUi() {
    carsViewModel.adapter = CarAdapter(this, mutableListOf())
    carsList.adapter = carsViewModel.adapter
    carsList.layoutManager = LinearLayoutManager(this)

    add.setOnClickListener {
      navigateToEditCar()
    }
  }

  private fun updateFromStatus(state: Resource<List<Car>>) {
    when (state.status) {
      Resource.Status.IDLE -> showLoading(false)
      Resource.Status.LOADING -> showLoading()
      Resource.Status.ERROR -> {
        state.error?.let {
          showToast(it.localizedMessage)
        }
      }
      Resource.Status.SUCCESS -> {
        state.data?.let { cars ->
          showCars(cars)
        }
      }
    }
  }

  private fun showCars(cars: List<Car>) {
    val hasCars = cars.isNotEmpty()
    carsViewModel.adapter.setItems(cars)
    carsList.visibility = if (hasCars) View.VISIBLE else View.GONE
    emptyResults.visibility = if (hasCars) View.GONE else View.VISIBLE
  }

  private fun navigateToCarDetails(id: Int) {
    startActivity(CarDetailsActivity.newIntent(this, id))
  }

  private fun navigateToEditCar(id: Int? = null) {
    startActivity(AddCarActivity.newIntent(this, id))
  }

  private fun navigateToCarPreferences(id: Int? = null) {
    showToast("feature coming soon!")
//    startActivity(CarPreferencesActivity.newIntent(this, id))
  }

  override fun onCarClicked(car: Car) {
    car.id?.let { id ->
      navigateToCarDetails(id)
    } ?: onError(NullPointerException("${javaClass.simpleName}.onCarClicked -> null car.id"))
  }

  override fun onCarActionClicked(@IdRes actionId: Int?, car: Car): Boolean {
    return when (actionId) {
      R.id.menu_edit -> {
        navigateToEditCar(car.id)
        true
      }
      R.id.menu_preferences -> {
        navigateToCarPreferences(car.id)
        true
      }
      R.id.menu_delete -> {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete)
            .setMessage(R.string.confirm_delete)
            .setNegativeButton(R.string.cancel) { dialog, _ -> dialog?.cancel() }
            .setPositiveButton(R.string.delete) { _, _ -> carsViewModel.deleteCar(car) }
        true
      }
      else -> false
    }
  }
}
