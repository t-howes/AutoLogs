package sample.thowes.autoservice.views.carList

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_car_list.*
import sample.thowes.autoservice.R

class CarListActivity : AppCompatActivity() {

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
      // TODO: show dialog or something
    }
  }

  private fun updateFromStatus(state: CarsViewModel.CarDetailsState) {
    when (state.status) {
      CarsViewModel.CarsStatus.LOADING -> {

      }
      CarsViewModel.CarsStatus.ERROR -> {

      }
      CarsViewModel.CarsStatus.EMPTY -> {

      }
      CarsViewModel.CarsStatus.SUCCESS -> {
        state.cars?.let { cars ->
          val adapter = CarAdapter(this, cars)
          carsList.adapter = adapter
          carsList.layoutManager = LinearLayoutManager(this)
        }
      }
    }
  }
}
