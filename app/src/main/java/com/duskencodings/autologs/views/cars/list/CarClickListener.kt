package com.duskencodings.autologs.views.cars.list

import androidx.annotation.IdRes
import com.duskencodings.autologs.models.Car

interface CarClickListener {
  fun onCarClicked(car: Car)
  fun onCarActionClicked(@IdRes actionId: Int?, car: Car): Boolean
}