package com.mongmong.namo.presentation.ui.auth

import android.app.Dialog
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

        // 오늘 날짜 이후 선택 불가: 최대 날짜를 오늘로 설정
        binding.registerDateDp.maxDate = Calendar.getInstance().timeInMillis

        // 초기 날짜 값이 있으면 DatePicker에 반영 (없으면 DatePicker 기본값은 오늘 날짜)
        if (initialYear.isNotEmpty() && initialMonth.isNotEmpty() && initialDay.isNotEmpty()) {
            binding.registerDateDp.updateDate(initialYear.toInt(), initialMonth.toInt(), initialDay.toInt())
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .create()

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

        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
