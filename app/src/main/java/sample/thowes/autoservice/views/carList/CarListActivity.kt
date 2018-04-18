package sample.thowes.autoservice.views.carList

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.activity_car_list.*
import sample.thowes.autoservice.R
import sample.thowes.autoservice.base.BaseActivity
import sample.thowes.autoservice.extensions.showToast
import sample.thowes.autoservice.views.carDetails.CarDetailsFragment
import sample.thowes.autoservice.views.carList.CarsViewModel.CarsStatus.*

class CarListActivity : BaseActivity() {

  private lateinit var carsViewModel: CarsViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_car_list)

    carsViewModel = ViewModelProviders.of(this).get(CarsViewModel::class.java)
    carsViewModel.state.observe(this, Observer<CarsViewModel.CarDetailsState> {
      it?.let { state ->
        updateFromStatus(state)
      }
    })

    add.setOnClickListener {
      supportFragmentManager
          .beginTransaction()
          .add(R.id.addCarContainer, CarDetailsFragment.newInstance(false))
          .addToBackStack("add_car")
          .commit()
    }
  }

  private fun updateFromStatus(state: CarsViewModel.CarDetailsState) {
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
          val adapter = CarAdapter(this, cars)
          carsList.adapter = adapter
          carsList.layoutManager = LinearLayoutManager(this)
          emptyResults.visibility = View.GONE
        }
      }
    }
  }
}
