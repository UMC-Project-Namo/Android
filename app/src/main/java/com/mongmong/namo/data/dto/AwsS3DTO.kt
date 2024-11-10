package com.mongmong.namo.data.dto

import com.mongmong.namo.data.utils.common.BaseResponse

data class GetPreSignedUrlResponse(
    val result: String
): BaseResponse()