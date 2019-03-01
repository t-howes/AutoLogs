package sample.thowes.autoservice.base

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import sample.thowes.autoservice.dagger.injector.Injector
import sample.thowes.autoservice.extensions.showToast
import sample.thowes.autoservice.models.SubscriptionHandler
import javax.inject.Inject

abstract class BaseFragment : Fragment(), BaseView, SubscriptionHandler {
  private var baseActivity: BaseActivity? = null
  private val disposables = CompositeDisposable()

  @Inject
  protected lateinit var vmFactory: ViewModelProvider.Factory

  override fun onAttach(context: Context) {
    super.onAttach(context)

    if (context is BaseActivity) {
      baseActivity = context
    } else {
      throw IllegalStateException("BaseFragment must attach to a BaseActivity")
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Injector.component.inject(this)
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

  protected inline fun <reified V : ViewModel> getViewModel(fragment: Fragment, key: String = ""): V {
    return if (key.isNotBlank()) {
      ViewModelProviders.of(fragment, vmFactory).get(key, V::class.java)
    } else {
      ViewModelProviders.of(fragment, vmFactory).get(V::class.java)
    }
  }

  protected inline fun <reified V : ViewModel> getViewModel(activity: FragmentActivity, key: String = ""): V {
    return if (key.isNotBlank()) {
      ViewModelProviders.of(activity, vmFactory).get(key, V::class.java)
    } else {
      ViewModelProviders.of(activity, vmFactory).get(V::class.java)
    }
  }
}