package com.mongmong.namo.presentation.ui.auth

import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.mongmong.namo.R
import com.mongmong.namo.databinding.ActivityRegisterBinding
import com.mongmong.namo.presentation.config.BaseActivity
import com.mongmong.namo.presentation.utils.hideKeyboardOnTouchOutside
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class RegisterActivity : BaseActivity<ActivityRegisterBinding>(R.layout.activity_register) {
    private val viewModel: RegisterViewModel by viewModels()

    override fun setup() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        initClickListener()
    }

    private fun initClickListener() {
        binding.registerProfileImgIv.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        binding.registerColorSelectLl.setOnClickListener {
            viewModel.setColor(1) // 색상 선택 기능
        }

        // 생년월일 선택 컨테이너 클릭 시 커스텀 다이얼로그 호출
        binding.registerBirthContainerLl.setOnClickListener {
            showRegisterDateDialog()
        }

        binding.registerSaveBtn.setOnClickListener {
            if (viewModel.isRegisterEnabled.value == true) {
                viewModel.requestRegister()
            } else {
                Toast.makeText(this, "색상과 필수 항목을 기재해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showRegisterDateDialog() {
        // 이미 선택한 날짜가 있다면 그 값을, 없으면 오늘 날짜를 기본값으로 사용
        val defaultYear = viewModel.getBirthYear()
        val defaultMonth = viewModel.getBirthMonth()
        val defaultDay = viewModel.getBirthDay()

        val dialog = RegisterDateDialog(defaultYear, defaultMonth, defaultDay) { selectedYear, selectedMonth, selectedDay ->
            // 선택한 날짜를 ViewModel에 저장 (필요에 따라 포맷 변경)
            viewModel.setBirthDate(
                selectedYear.toString(),
                String.format("%02d", selectedMonth + 1),
                String.format("%02d", selectedDay)
            )
        }
        dialog.show(supportFragmentManager, "RegisterDateDialog")
    }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                viewModel.setProfileImage(it)
            }
        }

    /** editText 외 터치 시 키보드 내리는 이벤트 **/
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        hideKeyboardOnTouchOutside(ev)
        return super.dispatchTouchEvent(ev)
    }
}
