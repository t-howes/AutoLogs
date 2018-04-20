package sample.thowes.autoservice.base

import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import sample.thowes.autoservice.R
import sample.thowes.autoservice.views.custom.LoadingView

abstract class BaseActivity : AppCompatActivity(), BaseView {

  private var loadingView: LoadingView? = null
  private val disposables = CompositeDisposable()

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

  protected fun addSub(disposable: Disposable) {
    disposables.add(disposable)
  }

  private fun clearDisposables() {
    if (!disposables.isDisposed) {
      disposables.dispose()
    }
  }
}