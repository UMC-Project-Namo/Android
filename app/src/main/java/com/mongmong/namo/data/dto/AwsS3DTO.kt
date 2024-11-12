package com.mongmong.namo.data.dto

import com.mongmong.namo.domain.model.BaseResponse

data class GetPreSignedUrlResponse(
    val result: String
): BaseResponse()