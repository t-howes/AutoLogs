package com.duskencodings.autologs.views.maintenance.upcoming

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.duskencodings.autologs.R
import com.duskencodings.autologs.models.UpcomingMaintenance


class UpcomingMaintenanceAdapter(context: Context,
                                 private val data: MutableList<UpcomingMaintenance> = mutableListOf(),
                                 private val onClick: (maintenance: UpcomingMaintenance) -> Unit) : RecyclerView.Adapter<UpcomingMaintenanceAdapter.UpcomingMaintenanceViewHolder>() {

  private val inflater = LayoutInflater.from(context)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpcomingMaintenanceViewHolder {
    return UpcomingMaintenanceViewHolder(inflater.inflate(R.layout.upcoming_maintenance, parent, false))
  }

  override fun onBindViewHolder(holder: UpcomingMaintenanceViewHolder, position: Int) {
    holder.bind(data[position])
  }

  override fun getItemCount(): Int = data.size


  inner class UpcomingMaintenanceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(maintenance: UpcomingMaintenance) {
      itemView.apply {
        // TODO: bind view

        setOnClickListener {
          onClick.invoke(maintenance)
        }
      }
    }
  }
}