package com.mongmong.namo.data.remote

import com.mongmong.namo.data.dto.GetProfileResponse
import retrofit2.http.GET

interface ProfileApiService {
    @GET("profile")
    suspend fun getProfile() : GetProfileResponse
}