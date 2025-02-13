package com.mongmong.namo.presentation.ui.setting.profile

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.mongmong.namo.databinding.DialogProfileEditDateBinding
import java.util.*

class ProfileEditDateDialog() : DialogFragment() {
    private val viewModel: ProfileEditViewModel by activityViewModels()
    private var _binding: DialogProfileEditDateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogProfileEditDateBinding.inflate(layoutInflater)
        val view = binding.root

        setupDatePicker()
        val dialog = createAlertDialog(view)
        initClickListeners(dialog)

        return dialog
    }

    private fun setupDatePicker() {
        binding.profileEditDateDp.maxDate = Calendar.getInstance().timeInMillis

        val dateParts = viewModel.birthday.value?.split("-")?.mapNotNull { it.toIntOrNull() }
        if (dateParts?.size == 3) {
            val (year, month, day) = dateParts
            binding.profileEditDateDp.updateDate(year, month - 1, day)  // month는 0-based로 변환
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
            val year = binding.profileEditDateDp.year
            val month = binding.profileEditDateDp.month  // 0부터 시작
            val day = binding.profileEditDateDp.dayOfMonth
            viewModel.setBirthday(
                year.toString(),
                String.format("%02d", month + 1),
                String.format("%02d", day)
            )
            dialog.dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
