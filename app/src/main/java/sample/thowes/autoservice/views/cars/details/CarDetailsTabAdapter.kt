package sample.thowes.autoservice.views.cars.details

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import sample.thowes.autoservice.R
import sample.thowes.autoservice.models.CarWork
import sample.thowes.autoservice.views.maintenance.list.MaintenanceListFragment

class CarDetailsTabAdapter(val context: Context,
                           fragManager: FragmentManager,
                           val carId: Int) : FragmentStatePagerAdapter(fragManager) {

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