package com.duskencodings.autologs.views.cars.details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_car_details.*
import com.duskencodings.autologs.R
import com.duskencodings.autologs.base.BaseActivity
import com.duskencodings.autologs.models.CAR_ID
import com.duskencodings.autologs.models.CAR_ID_DEFAULT

class CarDetailsActivity : BaseActivity() {
  private var carId: Int? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_car_details)

    val id = intent?.extras?.getInt(CAR_ID, CAR_ID_DEFAULT)

    if (CAR_ID_DEFAULT != id) {
      carId = id
    }

    initUi()
  }

  private fun initUi() {
    setDisplayHomeAsUpEnabled()

    carId?.let { id ->
      val detailsTabAdapter = CarDetailsTabAdapter(this, supportFragmentManager, id)
      detailsViewPager.adapter = detailsTabAdapter
      detailsTabs.setupWithViewPager(detailsViewPager)
    } ?: showAddForm()
  }

  private fun showAddForm() {
    supportFragmentManager
        .beginTransaction()
        .add(newCarLayout.id, CarDetailsFragment.newInstance())
//        .addToBackStack("new-car")
        .commit()
  }

  companion object {

    fun newIntent(context: Context, carId: Int?): Intent {
      val intent = Intent(context, CarDetailsActivity::class.java)
      carId?.let { id ->
        intent.putExtra(CAR_ID, id)
      }
      return intent
    }
  }
}