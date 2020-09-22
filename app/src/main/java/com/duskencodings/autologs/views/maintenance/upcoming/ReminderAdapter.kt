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
import com.duskencodings.autologs.utils.clearAndAdd
import com.duskencodings.autologs.utils.formatted
import com.duskencodings.autologs.utils.setTextOrHide
import kotlinx.android.synthetic.main.maintenance_reminder.view.*


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

  fun setReminders(reminders: List<Reminder>) {
    data.clearAndAdd(reminders)
    notifyDataSetChanged()
  }

  open inner class ReminderViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    @CallSuper
    open fun bind(reminder: Reminder) {
      itemView.apply {
        name.text = reminder.name
        due_on_miles.text = "${reminder.expireAtMiles} miles"
        description.setTextOrHide(reminder.description)
        due_on_date.setTextOrHide(reminder.expireAtDate.formatted())

        setOnClickListener {
          onClick.invoke(reminder)
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