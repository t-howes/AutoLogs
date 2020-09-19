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

  private var carId: Long = CAR_ID_DEFAULT // we will get passed a valid ID in the intent bundle.

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_car_details)
    init()
  }

  override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    init()
  }

  private fun init() {
    carId = intent?.extras?.getLong(CAR_ID, CAR_ID_DEFAULT) ?: CAR_ID_DEFAULT
    initUi()
  }

  private fun initUi() {
    setDisplayHomeAsUpEnabled()

    val detailsTabAdapter = CarDetailsTabAdapter(this, supportFragmentManager, carId)
    detailsViewPager.adapter = detailsTabAdapter
    detailsTabs.setupWithViewPager(detailsViewPager)
  }

  companion object {

    fun newIntent(context: Context, carId: Long): Intent {
      return Intent(context, CarDetailsActivity::class.java).apply {
        putExtra(CAR_ID, carId)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
      }
    }
  }
}