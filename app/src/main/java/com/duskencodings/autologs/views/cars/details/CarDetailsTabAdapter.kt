package com.duskencodings.autologs.views.cars.details

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.duskencodings.autologs.R
import com.duskencodings.autologs.views.maintenance.list.MaintenanceListFragment

class CarDetailsTabAdapter(val context: Context,
                           fragManager: FragmentManager,
                           val carId: Int) : FragmentStatePagerAdapter(fragManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

  override fun getItem(position: Int): Fragment {
    return when (position) {
      PAGE_DETAILS -> CarDetailsFragment.newInstance(carId)
      PAGE_MAINTENANCE -> MaintenanceListFragment.newInstance(carId)
      else -> CarDetailsFragment.newInstance(carId)
    }
  }

  override fun getPageTitle(position: Int): CharSequence? {
    return context.getString(when (position) {
      PAGE_DETAILS -> R.string.details
      PAGE_MAINTENANCE -> R.string.maintenance
      else -> R.string.other
    })
  }

  override fun getCount() = COUNT

  companion object {
    const val COUNT = 2
    const val PAGE_DETAILS = 0
    const val PAGE_MAINTENANCE = 1
    const val PAGE_FUEL = 2
  }
}