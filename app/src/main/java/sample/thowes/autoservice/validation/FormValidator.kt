package sample.thowes.autoservice.validation

import android.content.Context
import com.google.android.material.textfield.TextInputLayout
import sample.thowes.autoservice.R
import kotlin.math.abs

class FormValidator {

  companion object {

    fun validateRequired(context: Context?,
                         inputLayout: TextInputLayout): Boolean {

      val isValid = inputLayout.editText?.length() ?: 0 > 0

      inputLayout.error = if (isValid) null else {
        context?.getString(R.string.error_field_required)
      }

      inputLayout.isErrorEnabled = !isValid
      return isValid
    }

    fun hasLength(context: Context?,
                  inputLayout: TextInputLayout,
                  requiredLength: Int = 0): Boolean {

      val isValid = inputLayout.editText?.length() ?: 0 == abs(requiredLength)

      inputLayout.error = if (isValid) null else {
        context?.getString(R.string.error_invalid_length)
      }

      inputLayout.isErrorEnabled = !isValid
      return isValid
    }

    /**
     * Default check is that the length is not empty (at least 1 character).
     */
    fun hasAtLeastLength(context: Context?,
                         inputLayout: TextInputLayout,
                         requiredLength: Int = 1): Boolean {

      val isValid = inputLayout.editText?.length() ?: 0 >= abs(requiredLength)

      inputLayout.error = if (isValid) null else {
        context?.getString(R.string.error_invalid_length)
      }

      inputLayout.isErrorEnabled = !isValid
      return isValid
    }

  }
}