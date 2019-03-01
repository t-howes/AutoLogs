package sample.thowes.autoservice.base

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import sample.thowes.autoservice.R
import sample.thowes.autoservice.dagger.injector.Injector
import sample.thowes.autoservice.models.SubscriptionHandler
import sample.thowes.autoservice.views.custom.LoadingView
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