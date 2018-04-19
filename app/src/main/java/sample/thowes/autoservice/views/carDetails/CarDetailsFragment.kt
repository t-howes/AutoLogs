package sample.thowes.autoservice.views.carDetails

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_car_details.*
import sample.thowes.autoservice.R
import sample.thowes.autoservice.base.BaseFragment

class CarDetailsFragment : BaseFragment() {
  private var isEditing = IS_EDIT_DEFAULT

  private lateinit var carViewModel: CarViewModel

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.fragment_car_details, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    isEditing = arguments?.getBoolean(IS_EDIT) ?: IS_EDIT_DEFAULT

    submit.setOnClickListener {
      activity?.onBackPressed()
    }

    if (isEditing) {
      submit.text = getString(R.string.save)
      carViewModel = ViewModelProviders.of(this).get(CarViewModel::class.java)
      carViewModel.state.observe(this, Observer {
        it?.status.let {

        }
      })
      carViewModel.getCar(2003, "Hyundai", "Tiburon")
    }
  }

  companion object {
    private const val IS_EDIT = "isEdit"
    private const val IS_EDIT_DEFAULT = false

    fun newInstance(isEditing: Boolean = IS_EDIT_DEFAULT): CarDetailsFragment {
      val frag = CarDetailsFragment()
      val args = Bundle()
      args.putBoolean(IS_EDIT, isEditing)
      frag.arguments = args
      return frag
    }
  }
}