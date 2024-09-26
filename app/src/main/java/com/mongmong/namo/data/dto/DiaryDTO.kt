package com.mongmong.namo.data.dto

import com.mongmong.namo.presentation.config.BaseResponse

data class DiaryResponse(
    val result: String
) : BaseResponse()

data class GetDiaryResponse(
    val result: GetDiaryResult
): BaseResponse()

data class GetDiaryResult(
    val content: String,
    val diaryId: Long,
    val diaryImages: List<DiaryImage>,
    val enjoyRating: Int
)

data class DiaryImage(
    val diaryImageId: Long,
    val imageUrl: String,
    val orderNumber: Int
)

data class PostDiaryRequest(
    val content: String,
    val diaryImages: List<DiaryRequestImage>,
    val enjoyRating: Int,
    val scheduleId: Long
)

data class DiaryRequestImage(
    val imageUrl: String,
    val orderNumber: Int = 1
)

data class EditDiaryRequest(
    val content: String,
    val diaryImages: List<DiaryRequestImage>,
    val enjoyRating: Int,
    val deleteImages: List<Long>
)