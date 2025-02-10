package com.mongmong.namo.data.datasource.profile

import android.util.Log
import com.mongmong.namo.data.dto.GetProfileResponse
import com.mongmong.namo.data.dto.GetProfileResult
import com.mongmong.namo.data.remote.ProfileApiService
import com.mongmong.namo.data.utils.common.ErrorHandler.handleError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoteProfileDataSource @Inject constructor(
    private val apiService: ProfileApiService
) {
    suspend fun getProfile(): GetProfileResponse {
        var response = GetProfileResponse(result = GetProfileResult())
        withContext(Dispatchers.IO) {
            runCatching {
                apiService.getProfile()
            }.onSuccess {
                Log.d("RemoteProfileDataSource getProfile Success", "$it")
                response = it
            }.onFailure { exception ->
                Log.d("RemoteProfileDataSource getProfile Failure", "${exception.handleError()}")
            }
        }
        return response
    }
}