package com.mongmong.namo.data.dto

import com.mongmong.namo.presentation.config.BaseResponse

data class GetCalendarDiaryResponse(
    val result: GetCalendarDiaryResult
): BaseResponse()

data class GetCalendarDiaryResult(
    val year: Int = 1970,
    val month: Int = 1,
    val diaryDateForPersonal: List<String> = emptyList(),
    val diaryDateForMeeting: List<String> = emptyList(),
    val diaryDateForBirthday: List<String> = emptyList()
)

data class GetDiaryByDateResponse(
    val result: List<GetDiaryByDateResult>
): BaseResponse()

data class GetDiaryByDateResult(
    val categoryInfo: CategoryInfo,
    val participantInfo: DiaryCollectionParticipant,
    val scheduleEndDate: String,
    val scheduleStartDate: String,
    val scheduleType: Int,
    val scheduleTitle: String,
    val scheduleId: Long,
    val diaryId: Long
)