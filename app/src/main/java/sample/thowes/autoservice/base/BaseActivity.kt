package sample.thowes.autoservice.base

import android.support.v7.app.AppCompatActivity
import sample.thowes.autoservice.R
import sample.thowes.autoservice.views.custom.LoadingView

abstract class BaseActivity : AppCompatActivity() {

  private var loadingView: LoadingView? = null

  override fun setContentView(layoutResID: Int) {
    super.setContentView(layoutResID)
    loadingView = findViewById(R.id.loadingView)
  }

  protected fun showLoading(show: Boolean = true) {
    loadingView?.show(show)
  }
}