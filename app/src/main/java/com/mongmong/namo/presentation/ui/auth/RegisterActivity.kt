package com.mongmong.namo.presentation.ui.auth

import android.net.Uri
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.mongmong.namo.R
import com.mongmong.namo.databinding.ActivityRegisterBinding
import com.mongmong.namo.presentation.config.BaseActivity
import com.mongmong.namo.presentation.utils.hideKeyboardOnTouchOutside
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : BaseActivity<ActivityRegisterBinding>(R.layout.activity_register) {
    private val viewModel: RegisterViewModel by viewModels()

    override fun setup() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        setUserName()
        initClickListener()
    }

    private fun setUserName() {
        viewModel.setUserName(intent.getStringExtra("userName") ?: "사용자")
    }

    private fun initClickListener() {
        binding.registerProfileImgIv.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        binding.registerColorSelectLl.setOnClickListener {
            viewModel.clearHighlight("color")
            showColorDialog()
        }

        // 생년월일 선택 컨테이너 클릭 시 커스텀 다이얼로그 호출
        binding.registerBirthContentTv.setOnClickListener {
            viewModel.clearHighlight("birthDate")
            showRegisterDateDialog()
        }

        binding.registerSaveBtn.setOnClickListener {
            if (viewModel.isRegisterEnabled.value == true) {
                viewModel.requestRegister()
            } else {
                viewModel.enableHighlight()
                Toast.makeText(this, "색상과 필수 항목을 기재해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showRegisterDateDialog() {
        val birthDate = viewModel.birthDate.value ?: ""
        RegisterDateDialog(birthDate) { year, month, day ->
            viewModel.setBirthDate(
                year.toString(),
                String.format("%02d", month + 1),  // DatePicker는 0-based → 1-based 변환
                String.format("%02d", day)
            )
        }.show(supportFragmentManager, "RegisterDateDialog")
    }


    private fun showColorDialog() {
        val currentColor = viewModel.color.value
        val dialog = RegisterColorDialog(initialColor = currentColor) { selectedColor ->
            viewModel.setColor(selectedColor)
        }
        dialog.show(supportFragmentManager, "RegisterColorDialog")
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
