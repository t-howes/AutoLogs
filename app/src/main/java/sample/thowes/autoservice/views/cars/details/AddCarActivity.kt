package sample.thowes.autoservice.views.cars.details

import androidx.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.functions.Function3
import kotlinx.android.synthetic.main.activity_add_car.*
import sample.thowes.autoservice.R
import sample.thowes.autoservice.base.BaseActivity
import sample.thowes.autoservice.extensions.showToast
import sample.thowes.autoservice.models.*
import sample.thowes.autoservice.validation.FormValidator

class AddCarActivity : BaseActivity() {

  private var carId: Int? = null
  private lateinit var carViewModel: CarViewModel

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
    carViewModel.detailsState.observe(this, Observer {
      it?.let { data ->
        updateFromState(data)
      }
    })
    carViewModel.submitState.observe(this, Observer {
      it?.let { data ->
        listenForSubmit(data)
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

  private fun updateFromState(state: Resource<Car>) {
    when (state.status) {
      Resource.Status.IDLE -> showLoading(false)
      Resource.Status.LOADING -> showLoading()
      Resource.Status.ERROR -> {
        state.error?.let {
          showToast(it.localizedMessage)
        }
      }
      Resource.Status.SUCCESS -> {
        state.data?.let {
          showCarDetails(it)
        }
      }
    }
  }

  private fun listenForSubmit(state: Resource<Boolean>) {
    when (state.status) {
      Resource.Status.SUCCESS -> finish()
      Resource.Status.ERROR -> showToast(getString(R.string.error_occurred))
      else -> { /* not handling */ }
    }
  }

  private fun showCarDetails(car: Car) {
    setTitle(car.name ?: car.yearMakeModel())
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
      val intent = Intent(context, AddCarActivity::class.java)

      carId?.let { id ->
        intent.putExtra(CAR_ID, id)
      }

      return intent
    }
  }
}