package com.duskencodings.autologs.views.cars.add

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.functions.Function3
import kotlinx.android.synthetic.main.activity_add_car.*
import com.duskencodings.autologs.R
import com.duskencodings.autologs.base.BaseActivity
import com.duskencodings.autologs.models.*
import com.duskencodings.autologs.utils.formatted
import com.duskencodings.autologs.utils.visible
import com.duskencodings.autologs.validation.FormValidator

class AddCarActivity : BaseActivity() {

  private lateinit var carViewModel: AddCarViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_add_car)
    setDisplayHomeAsUpEnabled()

    carViewModel = getViewModel()
    val id = intent?.extras?.getLong(CAR_ID, CAR_ID_DEFAULT)

    if (CAR_ID_DEFAULT != id) {
      carViewModel.carId = id
    }

    initUi()

    carViewModel.state.subscribe {
      it?.let { data ->
        updateFromState(data)
      }
    }.also { addSub(it) }

    carViewModel.loadScreen()
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.menu_add_car, menu)
    menu?.getItem(0)?.subMenu?.let { submenu ->
      submenu.size().let { size ->
        for (i in 0 until size) {
          submenu.getItem(i)?.icon?.apply {
            DrawableCompat.setTint(this, ContextCompat.getColor(this@AddCarActivity, R.color.colorAccent))
          }
        }
      }
    }
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    return when(item?.itemId) {
      R.id.menu_select_photo -> {

        true
      }
      R.id.menu_take_photo -> {

        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  private fun initUi() {
    lastUpdate.visible = false

    if (carViewModel.carId == null) {
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
      AddCarViewModel.Status.IDLE -> showLoading(false)
      AddCarViewModel.Status.CAR_DETAILS -> state.car?.let { showCarDetails(it) }
      AddCarViewModel.Status.SAVED -> finish()
      AddCarViewModel.Status.ERROR -> state.error?.let { onError(it) }
    }
  }

  private fun showCarDetails(car: Car) {
    val name = car.name
    setTitle(name)
    nameInput.setText(car.nickname)
    yearInput.setText(car.year.toString())
    makeInput.setText(car.make)
    modelInput.setText(car.model)
    notesInput.setText(car.notes)
    lastUpdate.visible = true
    lastUpdate.text = getString(R.string.last_updated, car.lastUpdate.formatted())
  }

  private fun validForm(): Boolean {
    return (FormValidator.hasLength(this, yearLayout, 4)
        and FormValidator.validateRequired(this, makeLayout)
        and FormValidator.validateRequired(this, modelLayout))
  }

  private fun saveCar() {
    val name = nameInput.text.toString().let { if (it.isBlank()) null else it }
    val year = yearInput.text.toString().toInt()
    val make = makeInput.text.toString()
    val model = modelInput.text.toString()
    val notes = notesInput.text.toString()
    // null carId should auto generate an ID in Room
    val newCar = Car(carViewModel.carId, year, make, model, name, notes)
    carViewModel.updateCar(newCar)
  }

  companion object {

    fun newIntent(context: Context, carId: Long? = null): Intent {
      return Intent(context, AddCarActivity::class.java).apply {
        carId?.let { id ->
          putExtra(CAR_ID, id)
        }
      }
    }
  }
}