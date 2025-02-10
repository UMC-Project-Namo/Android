package com.mongmong.namo.presentation.ui.custom

import android.content.Intent
import android.net.Uri
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.mongmong.namo.R
import com.mongmong.namo.databinding.ActivityProfileEditBinding
import com.mongmong.namo.presentation.config.BaseActivity
import com.mongmong.namo.presentation.ui.MainActivity
import com.mongmong.namo.presentation.utils.hideKeyboardOnTouchOutside
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileEditActivity : BaseActivity<ActivityProfileEditBinding>(R.layout.activity_profile_edit) {
    private val viewModel: ProfileEditViewModel by viewModels()

    override fun setup() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        setProfileInfo()
        initClickListener()
        initObservers()
    }

    private fun setProfileInfo() {
        //viewModel.setProfileInfo()
    }

    private fun initClickListener() {
        binding.profileEditProfileImgIv.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        binding.profileEditColorCv.setOnClickListener {
            showColorDialog()
        }

        binding.profileEditBirthContentTv.setOnClickListener {
            showRegisterDateDialog()
        }

        binding.profileEditSaveBtn.setOnClickListener {
            viewModel.editProfile()
        }
    }

    private fun initObservers() {
        viewModel.isEditComplete.observe(this) { isComplete ->
            if(isComplete.isSuccess) {
                Toast.makeText(this, getString(R.string.profile_edit_success), Toast.LENGTH_SHORT).show()
                // viewModel.setInitialProfile() 성공하면 초기 프로필 데이터 갱신
            } else Toast.makeText(this, isComplete.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showRegisterDateDialog() {
        val dialog = ProfileEditDateDialog()
        dialog.show(supportFragmentManager, "ProfileEditDateDialog")
    }


    private fun showColorDialog() {
        val dialog = ProfileEditColorDialog()
        dialog.show(supportFragmentManager, "ProfileEditColorDialog")
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
