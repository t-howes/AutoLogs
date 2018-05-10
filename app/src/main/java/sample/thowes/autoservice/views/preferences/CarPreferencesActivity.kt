package sample.thowes.autoservice.views.preferences

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import sample.thowes.autoservice.R
import sample.thowes.autoservice.base.BaseActivity
import sample.thowes.autoservice.extensions.showToast
import sample.thowes.autoservice.models.CAR_ID
import sample.thowes.autoservice.models.CAR_ID_DEFAULT
import sample.thowes.autoservice.models.Car
import sample.thowes.autoservice.models.Resource
import sample.thowes.autoservice.views.cars.details.AddCarActivity
import sample.thowes.autoservice.views.cars.details.CarViewModel

class CarPreferencesActivity : BaseActivity() {

  private lateinit var carViewModel: CarViewModel
  private var carId: Int? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_car_preferences)
    val id = intent?.extras?.getInt(CAR_ID, CAR_ID_DEFAULT) ?: CAR_ID_DEFAULT

    if (CAR_ID_DEFAULT != id) {
      carId = id
    }

    initUi()

    carViewModel = ViewModelProviders.of(this).get(CarViewModel::class.java)
    carViewModel.detailsState.observe(this, Observer {
      it?.let {
        updateFromState(it)
      }
    })

    carViewModel.getCar(carId)
  }

  private fun initUi() {

  }

  private fun updateFromState(state: Resource<Car>) {
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
        intent.extras.putInt(CAR_ID, id)
      }

      return intent
    }
  }
}