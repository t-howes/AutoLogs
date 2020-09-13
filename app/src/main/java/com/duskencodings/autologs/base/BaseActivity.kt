package com.duskencodings.autologs.base

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import com.duskencodings.autologs.R
import com.duskencodings.autologs.dagger.injector.Injector
import com.duskencodings.autologs.utils.showToast
import com.duskencodings.autologs.models.SubscriptionHandler
import com.duskencodings.autologs.views.custom.LoadingView
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity(), BaseView, SubscriptionHandler {

  private var loadingView: LoadingView? = null
  private val disposables = CompositeDisposable()

  @Inject
  lateinit var vmFactory: ViewModelProvider.Factory

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Injector.component.inject(this)
  }

  override fun setContentView(layoutResID: Int) {
    super.setContentView(layoutResID)
    loadingView = findViewById(R.id.loading)
  }

  override fun onDestroy() {
    clearDisposables()
    super.onDestroy()
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    if (item?.itemId == android.R.id.home) {
      return onUpButtonClicked()
    }

    return super.onOptionsItemSelected(item)
  }

  protected open fun onUpButtonClicked(): Boolean {
    finish()
    return true
  }

  override fun showLoading(show: Boolean) {
    loadingView?.show(show)
  }

  override fun addSub(disposable: Disposable) {
    disposables.add(disposable)
  }

  override fun clearDisposables() {
    if (!disposables.isDisposed) {
      disposables.dispose()
    }
  }

  open fun onError(error: Throwable) {
    Log.e("BASE ACTIVITY", "onError() called:", error)
    showToast(R.string.error_occurred)
  }

  protected fun setDisplayHomeAsUpEnabled(show: Boolean = true) {
    supportActionBar?.setDisplayHomeAsUpEnabled(show)
  }

  inline fun <reified V : ViewModel> getViewModel(key: String = ""): V {
    return if (key.isNotBlank()) {
      ViewModelProviders.of(this, vmFactory).get(key, V::class.java)
    } else {
      ViewModelProviders.of(this, vmFactory).get(V::class.java)
    }
  }
}