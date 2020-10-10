package com.duskencodings.autologs.views.cars.details

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.duskencodings.autologs.R
import com.duskencodings.autologs.base.BaseFragment
import com.duskencodings.autologs.utils.formatMoney
import com.duskencodings.autologs.models.*
import com.duskencodings.autologs.utils.showToast
import com.duskencodings.autologs.utils.visible
import com.duskencodings.autologs.views.cars.add.AddCarActivity
import com.duskencodings.autologs.views.maintenance.upcoming.ReminderAdapter
import com.duskencodings.autologs.views.reminders.EditReminderDialogFragment
import kotlinx.android.synthetic.main.fragment_car_details.*

class CarDetailsFragment : BaseFragment() {

  private lateinit var carDetailsViewModel: CarDetailsViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setHasOptionsMenu(true)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.fragment_car_details, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val carId = arguments?.getLong(CAR_ID, CAR_ID_DEFAULT) ?: CAR_ID_DEFAULT
    init(carId)
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.car_actions, menu)
    super.onCreateOptionsMenu(menu, inflater)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when(item.itemId) {
      R.id.menu_edit -> {
        carDetailsViewModel.carId?.let { carId ->
          baseActivity.startActivity(AddCarActivity.newIntent(requireContext(), carId))
        } ?: showError(NullPointerException("Null carId! RIPPERONNI"))
        true
      }
      R.id.menu_preferences -> {
        context.showToast("feature coming soon!")
        true
      }
      R.id.menu_delete -> {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete)
            .setMessage(R.string.confirm_delete_car)
            .setNegativeButton(R.string.cancel) { dialog, _ -> dialog?.cancel() }
            .setPositiveButton(R.string.delete) { _, _ -> carDetailsViewModel.deleteCar() }
            .show()
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  private fun init(carId: Long) {
    carDetailsViewModel = getViewModel(this)
    carDetailsViewModel.state.subscribe {
      it?.let { state ->
        updateState(state)
      }
    }.also { addSub(it) }

    carDetailsViewModel.loadScreen(carId)
  }

  private fun updateState(state: CarDetailsViewModel.State) {
    when (state.status) {
      CarDetailsViewModel.Status.INIT_UI -> initUi()
      CarDetailsViewModel.Status.LOADING_DETAILS -> showLoading(true)
      CarDetailsViewModel.Status.LOADING_REMINDERS -> { /* TODO */ }
      CarDetailsViewModel.Status.CAR -> state.car?.let { onCarReceived(it) }
      CarDetailsViewModel.Status.CAR_DELETED -> requireActivity().finish()
      CarDetailsViewModel.Status.SPENDING -> state.spendingBreakdown?.let { onSpendingBreakdownReceived(it) }
      CarDetailsViewModel.Status.REMINDERS -> state.reminders?.let { onRemindersReceived(it) }
      CarDetailsViewModel.Status.ERROR_DETAILS,
      CarDetailsViewModel.Status.ERROR_REMINDERS -> state.error?.let { onError(it) }
    }
  }

  private fun initUi() {
    upcoming_reminders.apply {
      carDetailsViewModel.reminderAdapter = ReminderAdapter(context) {
        it.id?.let {  id ->
          EditReminderDialogFragment.show(requireContext(), id)
        } ?: onError(NullPointerException("Null reminderId"))
      }
      layoutManager = LinearLayoutManager(context)
      adapter = carDetailsViewModel.reminderAdapter
    }
  }

  private fun onCarReceived(car: Car) {
    setTitle(car.name)
    val hasNotes = !car.notes.isNullOrBlank()
    notes_group.visible = hasNotes
    notes.text = car.notes
  }

  private fun onSpendingBreakdownReceived(breakdown: SpendingBreakdown) {
    total_spent.text = breakdown.totalCost.formatMoney()
    mods_spent.text = breakdown.modsCost.formatMoney()
    maintenance_spent.text = breakdown.maintenanceCosts.formatMoney()
  }

  private fun onRemindersReceived(reminders: List<Reminder>) {
    reminders_label.text = getString(
        if (reminders.isEmpty()) R.string.no_upcoming_maintenance else R.string.upcoming_maintenance
    )
    due_on_label.visible = reminders.isNotEmpty()
    carDetailsViewModel.reminderAdapter.setReminders(reminders)
  }

  companion object {
    fun newInstance(carId: Long): CarDetailsFragment {
      return CarDetailsFragment().apply {
        arguments = Bundle().apply {
          putLong(CAR_ID, carId)
        }
      }
    }
  }
}