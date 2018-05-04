package sample.thowes.autoservice.views.maintenance.list

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_row_car_work.view.*
import sample.thowes.autoservice.R
import sample.thowes.autoservice.extensions.asDate
import sample.thowes.autoservice.models.CarWork

class MaintenanceAdapter(context: Context, var data: List<CarWork>)
      : RecyclerView.Adapter<MaintenanceAdapter.CarWorkViewHolder>() {

  private val inflater = LayoutInflater.from(context)
  private var onClickListener: ((maintenance: CarWork) -> Unit)? = null

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
        val showDate = position == 0 || work.date.asDate() != getItem(position - 1).date.asDate()

        itemView.dateContainer.visibility = if (showDate) View.VISIBLE else View.GONE
        itemView.date.text = work.date
        itemView.name.text = work.name
        itemView.container.setOnClickListener {
          onClickListener?.invoke(carWork)
        }
      }
    }
  }

}
