package sample.thowes.autoservice.views.carList

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_car_list.*
import sample.thowes.autoservice.R

class CarListActivity : AppCompatActivity() {

  private lateinit var carsViewModel: CarsViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_car_list)

    carsViewModel = ViewModelProviders.of(this).get(CarsViewModel::class.java)
    carsViewModel.getObservableCars().observe(this, Observer<CarsViewModel.CarsState> {

    })

    // TODO: mvvm and use view model to get cars
    val adapter = CarAdapter(this, cars)

    add.setOnClickListener {
      // TODO: show dialog or something
    }
  }
}
