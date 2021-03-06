package com.duskencodings.autologs.views.cars.list

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_row_car.view.*
import androidx.appcompat.widget.PopupMenu
import com.duskencodings.autologs.R
import com.duskencodings.autologs.extensions.clearAndAdd
import com.duskencodings.autologs.models.Car


class CarAdapter(context: Context, private val cars: MutableList<Car>) : RecyclerView.Adapter<CarViewHolder>() {
  private val inflater = LayoutInflater.from(context)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
    return CarViewHolder(inflater.inflate(R.layout.item_row_car, parent, false))
  }

  override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
    holder.bind(getItem(position))
  }

  override fun getItemCount() = cars.size
  fun getItem(position: Int) = cars[position]

  fun setItems(cars: List<Car>) {
    this.cars.clearAndAdd(cars)
    notifyDataSetChanged()
  }
}

class CarViewHolder(view: View) : RecyclerView.ViewHolder(view) {

  fun bind(car: Car?) {
    car?.let {
      val context = itemView.context
      itemView.name.text = car.name
      itemView.name.visibility = if (car.name.isNullOrBlank()) View.GONE else View.VISIBLE
      val yearMakeModel = "${car.year} ${car.make} ${car.model}"
      itemView.yearMakeModel.text = yearMakeModel
      itemView.container.setOnClickListener { (context as? CarClickListener)?.onCarClicked(car) }
      itemView.options.setOnClickListener {
        val popup = PopupMenu(itemView.context, itemView.options)
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.car_actions, popup.menu)
        popup.setOnMenuItemClickListener{
          (context as? CarClickListener)?.onCarActionClicked(it.itemId, car) ?: false
        }
        popup.show()
      }
    }
  }
}