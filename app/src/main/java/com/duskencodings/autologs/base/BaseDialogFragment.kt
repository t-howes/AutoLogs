package com.duskencodings.autologs.base

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.duskencodings.autologs.dagger.injector.Injector
import com.duskencodings.autologs.models.SubscriptionHandler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import javax.inject.Inject

abstract class BaseDialogFragment : DialogFragment(), BaseView, SubscriptionHandler {
  private lateinit var baseActivity: BaseActivity
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

  override fun addSub(disposable: Disposable) {
    disposables.add(disposable)
  }

  override fun clearDisposables() {
    if (!disposables.isDisposed) {
      disposables.clear()
    }
  }

  protected open fun onError(error: Throwable) {
    Log.e("BASE DIALOG FRAG", "onError() called:", error)
    baseActivity.onError(error)
  }

  protected inline fun <reified V : ViewModel> Fragment.getViewModel(fragment: Fragment, key: String = ""): V {
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