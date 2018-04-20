package sample.thowes.autoservice.base

import android.content.Context
import android.support.v4.app.Fragment

abstract class BaseFragment : Fragment(), BaseView {
  private var baseActivity: BaseActivity? = null

  override fun onAttach(context: Context?) {
    super.onAttach(context)

    if (context is BaseActivity) {
      baseActivity = context
    }
  }

  override fun showLoading(show: Boolean) {
    baseActivity?.showLoading(show)
  }
}