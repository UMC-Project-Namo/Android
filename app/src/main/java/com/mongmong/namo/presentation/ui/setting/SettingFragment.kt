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
        onClickListener()
    }

    private fun initObserve() {
        viewModel.profile.observe(viewLifecycleOwner) { profile ->
            if (profile == null) return@observe
            binding.profile = profile
        }

        viewModel.isLogoutComplete.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), "로그아웃에 성공하셨습니다.", Toast.LENGTH_SHORT).show()
            moveToLoginFragment() // 화면 이동
        }

        viewModel.isQuitComplete.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), "회원탈퇴에 성공하셨습니다.", Toast.LENGTH_SHORT).show()
            moveToLoginFragment() // 화면 이동
        }
    }


    private fun onClickListener() {
        binding.apply {
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
        // 다이얼로그
        val title = "로그아웃 하시겠어요?"

        val dialog = ConfirmDialog(this@SettingFragment, title, null, "확인", LOGOUT_ID)
        // 알림창이 띄워져있는 동안 배경 클릭 막기
        dialog.isCancelable = false
        activity?.let { dialog.show(it.supportFragmentManager, "ConfirmDialog") }
    }

    private fun quit() {
        // 다이얼로그
        val title = "정말 계정을 삭제하시겠어요?"
        val content = "지금까지의 정보는\n" +
                "3일 뒤 모두 사라집니다."

        val dialog = ConfirmDialog(this@SettingFragment, title, content, "확인", QUIT_ID)
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
