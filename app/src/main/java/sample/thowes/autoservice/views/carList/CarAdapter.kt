package sample.thowes.autoservice.views.carList

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_row_car.view.*
import sample.thowes.autoservice.R
import sample.thowes.autoservice.models.Car

class CarAdapter(context: Context, private val cars: List<Car>) : RecyclerView.Adapter<CarViewHolder>() {
  private val inflater = LayoutInflater.from(context)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
    return CarViewHolder(inflater.inflate(R.layout.item_row_car, parent, false))
  }

  override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
    holder.bind(getItem(position))
  }

  override fun getItemCount() = cars.size
  fun getItem(position: Int) = cars[position]
}

class CarViewHolder(view: View) : RecyclerView.ViewHolder(view) {

  fun bind(car: Car?) {
    car?.let {
      itemView.name.text = car.name
      itemView.name.visibility = if (car.name.isNullOrBlank()) View.GONE else View.VISIBLE
      itemView.year.text = car.year.toString()
      itemView.make.text = car.make
      itemView.model.text = car.model
      itemView.miles.text = itemView.context.getString(R.string.x_miles, car.miles)
      itemView.container.setOnClickListener { (itemView.context as? CarClickListener)?.onCarClicked(car) }
    }
  }
}