package com.mongmong.namo.presentation.di

import com.mongmong.namo.data.remote.ActivityApiService
import com.mongmong.namo.data.remote.AnonymousApiService
import com.mongmong.namo.data.remote.CategoryApiService
import com.mongmong.namo.data.remote.DiaryApiService
import com.mongmong.namo.data.remote.AuthApiService
import com.mongmong.namo.data.remote.AwsS3ApiService
import com.mongmong.namo.data.remote.FriendApiService
import com.mongmong.namo.data.remote.ImageApiService
import com.mongmong.namo.data.remote.ReissuanceApiService
import com.mongmong.namo.data.remote.MoimApiService
import com.mongmong.namo.data.remote.ProfileApiService
import com.mongmong.namo.data.remote.ScheduleApiService
import com.mongmong.namo.data.remote.TermApiService
import com.mongmong.namo.presentation.ui.home.schedule.map.data.KakaoAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    /** 익명 (로그인, 토큰 재발급) */
    @Provides
    @Singleton
    fun provideAnonymousService(@NetworkModule.AnonymousRetrofit retrofit: Retrofit) : AnonymousApiService =
        retrofit.create(AnonymousApiService::class.java)

    /** 인증 (로그아웃, 회원탈퇴) */
    @Provides
    @Singleton
    fun provideAuthService(@NetworkModule.BasicRetrofit retrofit: Retrofit) : AuthApiService =
        retrofit.create(AuthApiService::class.java)

    /** 토큰 재발급 (추후 삭제 예정) */
    @Provides
    @Singleton
    fun provideReissuanceService(@NetworkModule.ReissuanceRetrofit retrofit: Retrofit) : ReissuanceApiService =
        retrofit.create(ReissuanceApiService::class.java)

    /** 약관 */
    @Provides
    @Singleton
    fun provideTermService(@NetworkModule.BasicRetrofit retrofit: Retrofit) : TermApiService =
        retrofit.create(TermApiService::class.java)

    /** 일정 */
    @Provides
    @Singleton
    fun provideScheduleService(@NetworkModule.BasicRetrofit retrofit: Retrofit) : ScheduleApiService =
        retrofit.create(ScheduleApiService::class.java)

    /** 기록 */
    @Provides
    @Singleton
    fun provideDiaryService(@NetworkModule.BasicRetrofit retrofit: Retrofit) : DiaryApiService =
        retrofit.create(DiaryApiService::class.java)

    /** 활동 */
    @Provides
    @Singleton
    fun provideActivityService(@NetworkModule.BasicRetrofit retrofit: Retrofit): ActivityApiService =
        retrofit.create(ActivityApiService::class.java)

    /** 카테고리 */
    @Provides
    @Singleton
    fun provideCategoryService(@NetworkModule.BasicRetrofit retrofit: Retrofit) : CategoryApiService =
        retrofit.create(CategoryApiService::class.java)

    /** 모임 일정 */
    @Provides
    @Singleton
    fun provideMoimService(@NetworkModule.BasicRetrofit retrofit: Retrofit): MoimApiService =
        retrofit.create(MoimApiService::class.java)

    /** 친구 */
    @Provides
    @Singleton
    fun provideFriendService(@NetworkModule.BasicRetrofit retrofit: Retrofit): FriendApiService =
        retrofit.create(FriendApiService::class.java)

    /** 프로필 */
    @Provides
    @Singleton
    fun provideProfileService(@NetworkModule.BasicRetrofit retrofit: Retrofit): ProfileApiService =
        retrofit.create(ProfileApiService::class.java)

    /** 이미지 url  */
    @Provides
    @Singleton
    fun provideImageService(@NetworkModule.BasicRetrofit retrofit: Retrofit): ImageApiService =
        retrofit.create(ImageApiService::class.java)

    /** s3 이미지 업로드 */
    @Provides
    @Singleton
    fun provideAwsS3Service(@NetworkModule.AnonymousRetrofit retrofit: Retrofit): AwsS3ApiService =
        retrofit.create(AwsS3ApiService::class.java)

    /** 카카오 맵 **/
    @Provides
    @Singleton
    fun provideKakaoService(retrofit: Retrofit): KakaoAPI =
        retrofit.create(KakaoAPI::class.java)
}