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
    private val initialYear: String,
    private val initialMonth: String,
    private val initialDay: String,
    private val onDateSelected: (year: Int, month: Int, day: Int) -> Unit
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
        if (initialYear.isNotEmpty() && initialMonth.isNotEmpty() && initialDay.isNotEmpty()) {
            binding.registerDateDp.updateDate(initialYear.toInt(), initialMonth.toInt(), initialDay.toInt())
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
        // 취소 버튼: 다이얼로그 dismiss
        binding.dialogNoBtn.setOnClickListener {
            dialog.dismiss()
        }

        // 확인 버튼: 선택한 날짜를 콜백으로 전달 후 dismiss
        binding.dialogYesBtn.setOnClickListener {
            val year = binding.registerDateDp.year
            val month = binding.registerDateDp.month  // 월은 0부터 시작
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
