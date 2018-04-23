package sample.thowes.autoservice.views.cars.details

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.functions.Function3
import kotlinx.android.synthetic.main.fragment_car_basic_details.*
import sample.thowes.autoservice.R
import sample.thowes.autoservice.base.BaseFragment
import sample.thowes.autoservice.extensions.showToast
import sample.thowes.autoservice.models.Car
import sample.thowes.autoservice.models.yearMakeModel
import sample.thowes.autoservice.validation.FormValidator
import sample.thowes.autoservice.views.cars.details.CarDetailsActivity.Companion.CAR_ID
import sample.thowes.autoservice.views.cars.details.CarDetailsActivity.Companion.CAR_ID_DEFAULT

class CarDetailsFragment : BaseFragment() {

  private lateinit var carViewModel: CarViewModel
  private var carId: Int? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.fragment_car_basic_details, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val id = arguments?.getInt(CAR_ID, CAR_ID_DEFAULT) ?: CAR_ID_DEFAULT

    if (CAR_ID_DEFAULT != id) {
      carId = id
    }

    initUi()

    carViewModel = ViewModelProviders.of(this).get(CarViewModel::class.java)
    carViewModel.state.observe(this, Observer {
      it?.let {
        updateFromState(it)
      }
    })

    carViewModel.getCar(carId)
  }

  private fun initUi() {
    lastUpdate.visibility = View.GONE

    if (carId == null) {
      setTitle(getString(R.string.add_car))
    } else {
      submit.text = getString(R.string.save)
    }

    setupInlineValidations()

    submit.setOnClickListener {
      if (validForm()) {
        saveCar()
      }
    }
  }

  private fun setupInlineValidations() {
    //TODO: fix this shit
    val yearObservable = RxTextView.textChanges(yearInput)
        .skip(1)
        .map { inputText -> inputText.toString().length == 4 }

    val makeObservable = RxTextView.textChanges(makeInput)
        .skip(1)
        .map { inputText -> inputText.isNotEmpty() }

    val modelObservable = RxTextView.textChanges(modelInput)
        .skip(1)
        .map { inputText -> inputText.isNotEmpty() }

    addSub(Observable.zip(yearObservable, makeObservable, modelObservable,
        Function3<Boolean, Boolean, Boolean, Boolean> { isYearValid, isMakeValid, isModelValid ->
          isYearValid && isMakeValid && isModelValid
        }).subscribe{ isValid ->
      submit.isEnabled = isValid
    })
  }

  private fun updateFromState(state: CarViewModel.CarDetailsState) {
    showLoading(false)

    when (state.status) {
      CarViewModel.CarStatus.IDLE -> {}
      CarViewModel.CarStatus.LOADING -> showLoading()
      CarViewModel.CarStatus.ERROR -> {
        state.error?.let {
          context.showToast(it.localizedMessage)
        }
      }
      CarViewModel.CarStatus.CAR_RETRIEVED -> {
        state.car?.let {
          showCarDetails(it)
        }
      }
      CarViewModel.CarStatus.SUBMIT -> activity?.finish()
    }
  }

  private fun showCarDetails(car: Car) {
    setTitle(car.name ?: car.yearMakeModel())
    nameInput.setText(car.name)
    yearInput.setText(car.year.toString())
    makeInput.setText(car.make)
    modelInput.setText(car.model)
    milesInput.setText(car.miles.toString())
    notesInput.setText(car.notes)
    lastUpdate.visibility = View.VISIBLE
    lastUpdate.text = getString(R.string.last_updated, car.lastUpdate)
  }

  private fun validForm(): Boolean {
    return (FormValidator.hasLength(context, yearLayout, 4)
        and FormValidator.validateRequired(context, makeLayout)
        and FormValidator.validateRequired(context, modelLayout))
  }

  private fun saveCar() {
    val name = nameInput.text.toString()
    val year = yearInput.text.toString().toInt()
    val make = makeInput.text.toString()
    val model = modelInput.text.toString()
    val miles = milesInput.text.toString().toInt()
    val notes = notesInput.text.toString()
    // null carId should auto generate an ID in Room
    val newCar = Car(carId, year, make, model, miles, name, notes)
    carViewModel.updateCar(newCar)
  }

  companion object {
    fun newInstance(carId: Int? = null): CarDetailsFragment {
      val fragment = CarDetailsFragment()
      val args = Bundle()

      carId?.let { id ->
        args.putInt(CAR_ID, id)
      }

      fragment.arguments = args
      return fragment
    }
  }
}