package sample.thowes.autoservice.views.cars.list

import sample.thowes.autoservice.models.Car

interface CarClickListener {
  fun onCarClicked(car: Car)
}