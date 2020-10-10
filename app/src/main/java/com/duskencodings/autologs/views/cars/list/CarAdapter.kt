package com.duskencodings.autologs.views.cars.list

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import kotlinx.android.synthetic.main.item_row_car.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.duskencodings.autologs.R
import com.duskencodings.autologs.utils.clearAndAdd
import com.duskencodings.autologs.models.Car
import com.duskencodings.autologs.utils.formatted
import com.squareup.picasso.Picasso

class CarAdapter(context: Context, private val cars: MutableList<Car>) : RecyclerView.Adapter<CarAdapter.CarViewHolder>() {
  private val inflater = LayoutInflater.from(context)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
    return CarViewHolder(inflater.inflate(R.layout.item_row_car, parent, false))
  }

  override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
    holder.bind(getItem(position), position)
  }

  override fun getItemCount() = cars.size

  private fun getItem(position: Int) = cars[position]

  fun setItems(cars: List<Car>) {
    this.cars.clearAndAdd(cars)
    notifyDataSetChanged()
  }

  class CarViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(car: Car, position: Int) {
      val context = itemView.context
      itemView.photo.apply {
        Picasso.get().load(car.imageUri)
//            .centerCrop()
            .placeholder(ResourcesCompat.getDrawable(resources, R.drawable.ic_car_default_black_24, null)!!.apply {
              DrawableCompat.setTint(this, ContextCompat.getColor(context, getDefaultCarColor(position)))
            })
            .into(this)
      }
      itemView.name.text = car.name
      itemView.lastUpdate.text = context.getString(R.string.last_updated, car.lastUpdate.formatted())
      itemView.container.setOnClickListener { (context as? CarClickListener)?.onCarClicked(car) }
      itemView.options.setOnClickListener {
        val popup = PopupMenu(itemView.context, itemView.options)
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.car_actions, popup.menu)
        popup.setOnMenuItemClickListener {
          (context as? CarClickListener)?.onCarActionClicked(it.itemId, car) ?: false
        }
        popup.show()
      }
    }

    @ColorRes
    private fun getDefaultCarColor(position: Int): Int {
      return when (position % 5) {
        1 -> R.color.colorPrimary
        2 -> R.color.colorAccent
        3 -> R.color.colorPrimaryDark
        4 -> R.color.colorSecondaryAccent
        else -> R.color.darkGray
      }
    }
  }
}