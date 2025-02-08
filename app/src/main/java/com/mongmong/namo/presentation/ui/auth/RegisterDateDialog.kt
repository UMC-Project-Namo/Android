package com.mongmong.namo.presentation.ui.auth

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.mongmong.namo.databinding.DialogRegisterDateBinding
import java.util.*

class RegisterDateDialog(
    private val birthDate: String,
    private val onDateSelected: (Int, Int, Int) -> Unit
) : DialogFragment() {

    private var _binding: DialogRegisterDateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogRegisterDateBinding.inflate(layoutInflater)
        val view = binding.root

        setupDatePicker()
        val dialog = createAlertDialog(view)
        initClickListeners(dialog)

        return dialog
    }

    private fun setupDatePicker() {
        binding.registerDateDp.maxDate = Calendar.getInstance().timeInMillis

        val dateParts = birthDate.split("-").mapNotNull { it.toIntOrNull() }
        if (dateParts.size == 3) {
            val (year, month, day) = dateParts
            binding.registerDateDp.updateDate(year, month - 1, day)  // month는 0-based로 변환
        }
    }

    private fun createAlertDialog(view: android.view.View): Dialog {
        return AlertDialog.Builder(requireContext())
            .setView(view)
            .create().apply {
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
    }

    private fun initClickListeners(dialog: Dialog) {
        binding.dialogNoBtn.setOnClickListener { dialog.dismiss() }

        binding.dialogYesBtn.setOnClickListener {
            val year = binding.registerDateDp.year
            val month = binding.registerDateDp.month  // 0부터 시작
            val day = binding.registerDateDp.dayOfMonth
            onDateSelected(year, month, day)
            dialog.dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
