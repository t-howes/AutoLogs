package sample.thowes.autoservice.views.carList

import sample.thowes.autoservice.models.Car

interface CarClickListener {
  fun onCarClicked(car: Car)
}