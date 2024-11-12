package com.mongmong.namo.data.utils.common

import com.mongmong.namo.domain.model.ActionResponse
import org.json.JSONObject
import retrofit2.HttpException

object ErrorHandler {
    // Throwable의 확장 함수로 에러 메시지 처리
    fun Throwable.handleError(): ActionResponse {
        return when (this) {
            is HttpException -> {
                val errorBody = this.response()?.errorBody()?.string()
                val errorMessage = try {
                    JSONObject(errorBody).getString("message")
                } catch (e: Exception) {
                    "Unknown HTTP error"
                }
                ActionResponse(
                    code = this.code(),
                    message = errorMessage,
                    isSuccess = false
                )
            }
            else -> {
                ActionResponse(
                    code = -1,
                    message = this.message ?: "Unknown error",
                    isSuccess = false
                )
            }
        }
    }

}