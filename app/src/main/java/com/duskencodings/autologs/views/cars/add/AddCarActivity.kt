package com.duskencodings.autologs.views.cars.add

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.functions.Function3
import kotlinx.android.synthetic.main.activity_add_car.*
import com.duskencodings.autologs.R
import com.duskencodings.autologs.base.BaseActivity
import com.duskencodings.autologs.models.*
import com.duskencodings.autologs.validation.FormValidator

class AddCarActivity : BaseActivity() {

  private var carId: Int? = null
  private lateinit var carViewModel: AddCarViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_add_car)
    setDisplayHomeAsUpEnabled()

    val id = intent?.extras?.getInt(CAR_ID, CAR_ID_DEFAULT)

    if (CAR_ID_DEFAULT != id) {
      carId = id
    }

    initUi()

    carViewModel = getViewModel()
    carViewModel.state.subscribe {
      it?.let { data ->
        updateFromState(data)
      }
    }.also { addSub(it) }

    carViewModel.loadScreen(carId)
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
    val yearObservable = RxTextView.textChanges(yearInput)
        .map { inputText -> inputText.toString().length == 4 }

    val makeObservable = RxTextView.textChanges(makeInput)
        .map { inputText -> inputText.isNotEmpty() }

    val modelObservable = RxTextView.textChanges(modelInput)
        .map { inputText -> inputText.isNotEmpty() }

    addSub(Observable.combineLatest(yearObservable, makeObservable, modelObservable,
        Function3<Boolean, Boolean, Boolean, Boolean> { isYearValid, isMakeValid, isModelValid ->
          isYearValid && isMakeValid && isModelValid
        }).subscribe { isValid ->
          submit.isEnabled = isValid
        }
    )
  }

  private fun updateFromState(state: AddCarViewModel.State) {
    when (state.status) {
      AddCarViewModel.Status.LOADING -> showLoading(true)
      AddCarViewModel.Status.CAR_DETAILS -> state.car?.let { showCarDetails(it) }
      AddCarViewModel.Status.SAVED -> finish()
      AddCarViewModel.Status.ERROR -> state.error?.let { onError(it) }
    }
  }

  private fun showCarDetails(car: Car) {
    val name = car.name
    setTitle(name)
    nameInput.setText(car.name)
    yearInput.setText(car.year.toString())
    makeInput.setText(car.make)
    modelInput.setText(car.model)
    notesInput.setText(car.notes)
    lastUpdate.visibility = View.VISIBLE
    lastUpdate.text = getString(R.string.last_updated, car.lastUpdate)
  }

  private fun validForm(): Boolean {
    return (FormValidator.hasLength(this, yearLayout, 4)
        and FormValidator.validateRequired(this, makeLayout)
        and FormValidator.validateRequired(this, modelLayout))
  }

  private fun saveCar() {
    val name = nameInput.text.toString()
    val year = yearInput.text.toString().toInt()
    val make = makeInput.text.toString()
    val model = modelInput.text.toString()
    val notes = notesInput.text.toString()
    // null carId should auto generate an ID in Room
    val newCar = Car(carId, year, make, model, name, notes)
    carViewModel.updateCar(newCar)
  }

  companion object {

    fun newIntent(context: Context, carId: Int? = null): Intent {
      return Intent(context, AddCarActivity::class.java).apply {
        carId?.let { id ->
          putExtra(CAR_ID, id)
        }
      }
    }
  }

}