package com.mongmong.namo.presentation.ui.login

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mongmong.namo.BuildConfig
import com.mongmong.namo.presentation.ui.MainActivity
import com.mongmong.namo.R
import com.mongmong.namo.presentation.config.ApplicationClass
import com.mongmong.namo.data.remote.auth.*
import com.mongmong.namo.databinding.FragmentLoginBinding
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.mongmong.namo.domain.model.LoginResponse
import com.mongmong.namo.domain.model.TokenBody
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment: Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var alarmManager : AlarmManager
    private lateinit var notificationManager : NotificationManager

    private val viewModel : AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)

        notificationManager = requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        initObserve()
        kakaoLogin()
        clickHandler()

        return binding.root
    }

    private fun clickHandler(){
        // 네이버 로그인
        binding.loginNaverBt.setOnClickListener {
            startNaverLogin()
        }
    }

    private fun initObserve() {
        viewModel.tokenResult.observe(viewLifecycleOwner) {
            if (it != null) {
                setLoginFinished()
            }
        }
    }

    private fun kakaoLogin() {
        // 카카오계정 로그인 공통 callback 구성
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {  //토큰 에러
                Log.e(ContentValues.TAG, "카카오계정으로 로그인 실패", error)
                // 카카오톡 설치는 되어있지만, 로그인이 안 되어있는 경우 예외 처리
                if (error.toString().contains("statusCode=302")){
                    loginWithKakaoAccount()
                }
            } else if (token != null) {
                Log.i(ContentValues.TAG, "카카오계정으로 로그인 성공 ${token.accessToken}")

                tryKakaoLogin(token.accessToken, token.refreshToken)
//                Log.d("kakao_access_token", token.accessToken)
//                Log.d("kakao_refresh_token", token.refreshToken)
            }

        }
        // 카카오 로그인 버튼 클릭시 로그인
        binding.loginKakaoBt.setOnClickListener {
            // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(requireContext())) {
                UserApiClient.instance.loginWithKakaoTalk(requireContext(), callback = callback)
            } else {
                loginWithKakaoAccount()
            }
        }
    }

    private fun loginWithKakaoAccount() {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (token != null) {
                tryKakaoLogin(token.accessToken, token.refreshToken)
            }
        }
        UserApiClient.instance.loginWithKakaoAccount(requireContext(), callback = callback)
    }

    private fun tryKakaoLogin(accessToken: String, refreshToken: String) {
        viewModel.tryKakaoLogin(accessToken, refreshToken)
    }

    private fun tryNaverLogin(accessToken: String, refreshToken: String) {
        viewModel.tryNaverLogin(accessToken, refreshToken)
    }

    private fun startNaverLogin() {
        // 네이버 로그인 모듈 초기화
        NaverIdLoginSDK.initialize(
            requireContext(), BuildConfig.NAVER_CLIENT_ID, BuildConfig.NAVER_CLIENT_SECRET, "나모"
        )

        // OAuthLoginCallback을 authenticate() 메서드 호출 시 파라미터로 전달하거나 NidOAuthLoginButton 객체에 등록하면 인증이 종료됨
        val oauthLoginCallback = object : OAuthLoginCallback {
            override fun onSuccess() {
                val naverAccessToken = NaverIdLoginSDK.getAccessToken().toString()
                val naverRefreshToken = NaverIdLoginSDK.getRefreshToken().toString()

                tryNaverLogin(naverAccessToken, naverRefreshToken)
//                Log.d("naver_access_token", naverAccessToken)
//                Log.d("naver_refresh_token",naverRefreshToken)
            }

            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
//                Toast.makeText(requireActivity(), "errorCode: $errorCode, errorDesc: $errorDescription", Toast.LENGTH_SHORT).show()
            }

            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }
        }
        NaverIdLoginSDK.authenticate(requireContext(), oauthLoginCallback)
    }

    private fun setLoginFinished(){
        requireActivity().finish()
        startActivity(Intent(requireContext(), MainActivity::class.java))
    }
}