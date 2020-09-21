package com.duskencodings.autologs.views.maintenance.list

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_row_car_work.view.*
import com.duskencodings.autologs.R
import com.duskencodings.autologs.models.CarWork
import com.duskencodings.autologs.utils.formatted
import com.duskencodings.autologs.utils.setTextOrHide
import com.duskencodings.autologs.utils.visible

class MaintenanceAdapter(
    context: Context,
    var data: List<CarWork>,
    var onClickListener: ((maintenance: CarWork) -> Unit)? = null
  ) : RecyclerView.Adapter<MaintenanceAdapter.CarWorkViewHolder>() {

  private val inflater = LayoutInflater.from(context)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarWorkViewHolder {
    return CarWorkViewHolder(inflater.inflate(R.layout.item_row_car_work, parent, false))
  }

  override fun onBindViewHolder(holder: CarWorkViewHolder, position: Int) {
    holder.bind(getItem(position))
  }

  override fun getItemCount() = data.size
  fun getItem(position: Int) = data[position]
  fun indexOf(carWork: CarWork) = data.indexOf(carWork)

  fun setOnMaintenanceClickedListener(onClick: (maintenance: CarWork) -> Unit) {
    onClickListener = onClick
  }

  inner class CarWorkViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(carWork: CarWork?) {
      carWork?.let { work ->
        val position = indexOf(work)
        val showDate = position == 0 || work.date != getItem(position - 1).date

        itemView.apply {
          dateContainer.visible = showDate
          defaultDivider.visible = !showDate
          date.text = work.date.formatted()
          name.text = work.name
          miles.text = work.odometerReading.toString()
          notes.setTextOrHide(work.notes)
          container.setOnClickListener {
            onClickListener?.invoke(carWork)
          }
        }
      }
    }
  }

}
