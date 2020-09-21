package com.duskencodings.autologs.views.custom

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.duskencodings.autologs.R
import com.duskencodings.autologs.utils.visible

class LoadingView : LinearLayout {

  private lateinit var loadingView: View
  private lateinit var messageView: TextView

  constructor(context: Context) : super(context) {}

  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}

  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

  init {
    init()
  }

  fun init() {
    val view = View.inflate(context, R.layout.view_loading, this)
    loadingView = view.findViewById(R.id.loadingView)
    messageView = view.findViewById(R.id.message)
  }

  fun show(show: Boolean = true) {
    loadingView.visible = show
  }

  fun setMessage(text: String? = context.getString(R.string.loading)) {
    messageView.text = text
  }
}
