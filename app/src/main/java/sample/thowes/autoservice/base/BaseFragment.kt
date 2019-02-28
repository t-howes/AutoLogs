package sample.thowes.autoservice.base

import android.content.Context
import androidx.fragment.app.Fragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import sample.thowes.autoservice.extensions.showToast
import sample.thowes.autoservice.models.SubscriptionHandler

abstract class BaseFragment : Fragment(), BaseView, SubscriptionHandler {
  private var baseActivity: BaseActivity? = null
  private val disposables = CompositeDisposable()

  override fun onAttach(context: Context?) {
    super.onAttach(context)

    if (context is BaseActivity) {
      baseActivity = context
    } else {
      throw IllegalStateException("BaseFragment must attach to a BaseActivity")
    }
  }

  override fun onDestroy() {
    clearDisposables()
    super.onDestroy()
  }

  override fun showLoading(show: Boolean) {
    baseActivity?.showLoading(show)
  }

  protected fun setTitle(title: String) {
    baseActivity?.title = title
  }

  override fun addSub(disposable: Disposable) {
    disposables.add(disposable)
  }

  override fun clearDisposables() {
    if (!disposables.isDisposed) {
      disposables.clear()
    }
  }

  protected fun showToast(message: String?) {
    context.showToast(message)
  }
}