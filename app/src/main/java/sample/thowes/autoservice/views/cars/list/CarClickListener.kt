package sample.thowes.autoservice.views.cars.list

import androidx.annotation.IdRes
import sample.thowes.autoservice.models.Car

interface CarClickListener {
  fun onCarClicked(car: Car)
  fun onCarActionClicked(@IdRes actionId: Int?, car: Car): Boolean
}