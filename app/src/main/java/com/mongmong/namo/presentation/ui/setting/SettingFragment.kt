package com.mongmong.namo.presentation.ui.setting

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.mongmong.namo.R
import com.mongmong.namo.databinding.FragmentSettingBinding
import com.mongmong.namo.presentation.config.ApplicationClass
import com.mongmong.namo.presentation.config.ApplicationClass.Companion.dsManager
import com.mongmong.namo.presentation.config.BaseFragment
import com.mongmong.namo.presentation.config.Constants
import com.mongmong.namo.presentation.ui.common.ConfirmDialog
import com.mongmong.namo.presentation.ui.common.ConfirmDialog.ConfirmDialogInterface
import com.mongmong.namo.presentation.ui.onBoarding.OnBoardingActivity
import com.mongmong.namo.presentation.ui.setting.profile.ProfileEditActivity
import com.mongmong.namo.presentation.ui.setting.profile.ProfileEditActivity.Companion.PROFILE_KEY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class SettingFragment: BaseFragment<FragmentSettingBinding>(R.layout.fragment_setting), ConfirmDialogInterface {
    private val viewModel: SettingViewModel by viewModels()

    override fun setup() {
        binding.version = viewModel.version
        initObserve()
    }

    override fun onResume() {
        super.onResume()
        initClickListeners()
    }

    private fun initObserve() {
        viewModel.profile.observe(viewLifecycleOwner) { profile ->
            if (profile == null) return@observe
            binding.profile = profile
        }

        viewModel.isLogoutComplete.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), getString(R.string.toast_setting_logout_success), Toast.LENGTH_SHORT).show()
            moveToLoginFragment() // 화면 이동
        }

        viewModel.isQuitComplete.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), getString(R.string.toast_setting_quit_success), Toast.LENGTH_SHORT).show()
            moveToLoginFragment() // 화면 이동
        }
    }

    private fun initClickListeners() {
        binding.apply {
            // 프로필 편집
            settingProfileEditBtn.setOnClickListener {
                startActivity(
                    Intent(context, ProfileEditActivity::class.java)
                        .putExtra(PROFILE_KEY, viewModel.profile.value)
                )
            }

            // 이용 약관
            settingTermTv.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.TERM_URL))
                startActivity(intent)
            }

            // 개인정보 처리방침
            settingIndividualPolicyTv.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.POLICY_URL))
                startActivity(intent)
            }

            // 로그아웃
            settingLogoutTv.setOnClickListener {
                logout()
            }

            // 회원탈퇴
            settingQuitTv.setOnClickListener {
                quit()
            }
        }
    }

    private fun logout() {
        val dialog = ConfirmDialog(
            this@SettingFragment,
            getString(R.string.dialog_setting_logout_title),
            null,
            getString(R.string.dialog_confirm),
            LOGOUT_ID
        )
        // 알림창이 띄워져있는 동안 배경 클릭 막기
        dialog.isCancelable = false
        activity?.let { dialog.show(it.supportFragmentManager, "ConfirmDialog") }
    }

    private fun quit() {
        val dialog = ConfirmDialog(
            this@SettingFragment,
            getString(R.string.dialog_setting_quit_title),
            getString(R.string.dialog_setting_quit_content),
            getString(R.string.dialog_confirm),
            QUIT_ID
        )
        // 알림창이 띄워져있는 동안 배경 클릭 막기
        dialog.isCancelable = false
        activity?.let { dialog.show(it.supportFragmentManager, "ConfirmDialog") }
    }

    private fun moveToLoginFragment() {
        // 토큰 비우기
        ApplicationClass.sSharedPreferences.edit().clear().apply()
        runBlocking { dsManager.clearAllData() }
        // 화면 이동
        activity?.finishAffinity()
        startActivity(Intent(context, OnBoardingActivity()::class.java))
    }


    override fun onClickYesButton(id: Int) { // 다이얼로그 확인 메시지 클릭
        when (id) {
            LOGOUT_ID -> viewModel.tryLogout()
            QUIT_ID -> viewModel.tryQuit()
        }
    }

    companion object {
        private const val LOGOUT_ID = 0
        private const val QUIT_ID = 1
    }
}
