package sample.thowes.autoservice.views.carDetails

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.functions.Function3
import kotlinx.android.synthetic.main.activity_car_details.*
import sample.thowes.autoservice.R
import sample.thowes.autoservice.base.BaseActivity
import sample.thowes.autoservice.extensions.showToast
import sample.thowes.autoservice.models.Car
import sample.thowes.autoservice.validation.FormValidator
import java.util.concurrent.TimeUnit

class CarDetailsActivity : BaseActivity() {

  private lateinit var carViewModel: CarViewModel
  private var carId: Int? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_car_details)
    val id = intent?.extras?.getInt(CAR_ID, -1)

    if (id != -1) {
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
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    lastUpdate.visibility = View.GONE

    if (carId == null) {
      setTitle(R.string.add_car)
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
          showToast(it.localizedMessage)
        }
      }
      CarViewModel.CarStatus.CAR_RETRIEVED -> {
        state.car?.let {
          showCarDetails(it)
        }
      }
      CarViewModel.CarStatus.SUBMIT -> finish()
    }
  }

  private fun showCarDetails(car: Car) {
    title = car.name
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
    return (FormValidator.hasLength(this, yearLayout, 4)
            and FormValidator.validateRequired(this, makeLayout)
            and FormValidator.validateRequired(this, modelLayout))
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
    private const val CAR_ID = "carId"

    fun newIntent(context: Context, carId: Int? = null): Intent {
      val intent = Intent(context, CarDetailsActivity::class.java)
      intent.putExtra(CAR_ID, carId)
      return intent
    }
  }
}