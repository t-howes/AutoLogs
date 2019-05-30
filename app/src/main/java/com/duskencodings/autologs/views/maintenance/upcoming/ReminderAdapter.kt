package com.duskencodings.autologs.views.maintenance.upcoming

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView
import com.duskencodings.autologs.R
import com.duskencodings.autologs.models.Reminder
import com.duskencodings.autologs.models.ReminderType


class ReminderAdapter(context: Context,
                      private val data: MutableList<Reminder> = mutableListOf(),
                      private val onClick: (reminder: Reminder) -> Unit) : RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

  private val inflater = LayoutInflater.from(context)

  // instead of weird Int mapping, just use its position and check type when creating the VH
  override fun getItemViewType(position: Int): Int = position

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
    return when (data[viewType].type) {
      ReminderType.UPCOMING_MAINTENANCE -> MaintenanceReminderViewHolder(inflater.inflate(R.layout.maintenance_reminder, parent, false))
      else /* BASIC */ -> ReminderViewHolder(inflater.inflate(R.layout.basic_reminder, parent, false))
    }
  }

  override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
    holder.bind(data[position])
  }

  override fun getItemCount(): Int = data.size

  open inner class ReminderViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    @CallSuper
    open fun bind(maintenance: Reminder) {
      itemView.apply {
        // TODO: bind view

        setOnClickListener {
          onClick.invoke(maintenance)
        }
      }
    }
  }

  inner class MaintenanceReminderViewHolder(view: View) : ReminderViewHolder(view) {

    override fun bind(maintenance: Reminder) {
      super.bind(maintenance)
    }
  }
}