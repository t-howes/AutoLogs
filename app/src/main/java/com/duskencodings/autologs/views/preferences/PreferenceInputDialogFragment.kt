package com.duskencodings.autologs.views.preferences

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.annotation.ArrayRes
import androidx.fragment.app.DialogFragment
import com.duskencodings.autologs.R
import com.duskencodings.autologs.base.BaseActivity
import com.duskencodings.autologs.models.CarWork
import kotlinx.android.synthetic.main.fragment_preference_input.*
import kotlinx.android.synthetic.main.item_row_car_preference.*

class PreferenceInputDialogFragment : DialogFragment() {

  private lateinit var prefListener: PreferenceInputListener

  override fun onAttach(context: Context) {
    super.onAttach(context)
    prefListener = context as PreferenceInputListener
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.fragment_preference_input, container)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val carWork: CarWork = arguments?.getParcelable(CAR_WORK)!!
    header.text = carWork.name
    setupSpinnerAdapter(spinner, R.array.preferences_selection)
    submit.setOnClickListener {
      val miles = milesInput.text.toString().toIntOrNull()
      val months = monthsInput.text.toString().toIntOrNull()

      if (miles == null || months == null) {
        (activity as BaseActivity).onError(IllegalArgumentException("Miles or months input is invalid"))
      } else {
        prefListener.onPreferenceSet(carWork, miles, months)
      }
    }
  }

  private fun setupSpinnerAdapter(spinner: Spinner, @ArrayRes arrayRes: Int) {
    val items = resources.getStringArray(arrayRes)
    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    spinner.adapter = adapter
    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val isOther = position == items.size - 1
        val item = items[position]
        val pair = item.split("/")
        val miles = parseInt(pair.first())
        val months = parseInt(pair.last())

        milesInput.setText(miles)
        monthsInput.setText(months)
        customInputContainer.visibility = if (isOther) View.VISIBLE else View.GONE
      }

      override fun onNothingSelected(parent: AdapterView<*>?) { }
    }
  }

  private fun parseInt(s: String): String? = s.trim().split(" ").first().toIntOrNull()?.toString()

  companion object {
    private const val CAR_WORK = "car_work"

    fun show(context: Context, carWork: CarWork) {
      val fragManager = (context as BaseActivity).supportFragmentManager
      PreferenceInputDialogFragment().apply {
        arguments = Bundle().apply {
          putParcelable(CAR_WORK, carWork)
        }
      }.show(fragManager, PreferenceInputDialogFragment::class.java.name)
    }
  }
}

interface PreferenceInputListener {
  fun onPreferenceSet(carWork: CarWork, miles: Int, months: Int)
}