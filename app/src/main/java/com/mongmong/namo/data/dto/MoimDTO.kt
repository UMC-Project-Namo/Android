package com.mongmong.namo.data.dto

/** 모임 일정 목록 조회 */
data class GetMoimResponse(
    val result: List<GetMoimResult>
)

data class GetMoimResult(
    val meetingScheduleId: Long = 0,
    val title: String = "",
    val startDate: Long = 0L,
    val imageUrl: String = "",
    val participantCount: Int = 0,
    val participantNicknames: String = ""
)

/** 모임 일정 상세 조회 */
data class GetMoimDetailResponse(
    val result: GetMoimDetailResult
)

data class GetMoimDetailResult(
    val scheduleId: Long = 0,
    val title: String = "",
    val imageUrl: String = "",
    val startDate: Long = 0L,
    val endDate: Long = 0L,
    val locationInfo: ScheduleLocation = ScheduleLocation(),
    val participants: List<MoimParticipant> = emptyList()
)

data class MoimParticipant(
    val participantId: Long = 0L,
    val userId: Long = 0L,
    val isGuest: Boolean = false,
    val nickname: String = "",
    val colorId: Int = 0,
    val isOwner: Boolean = false
)

/** 모임 캘린더 조회 */
data class GetMoimCalendarResponse(
    val result: ArrayList<GetMoimCalendarResult>
)

data class GetMoimCalendarResult(
    val scheduleId: Long = 0L,
    val title: String = "",
    val startDate: Long = 0L,
    val endDate: Long = 0L,
    val participants: List<CalendarParticipant> = emptyList(),
    val isCurMeetingSchedule: Boolean = false
)

data class CalendarParticipant(
    val participantId: Long = 0L,
    val userId: Long = 0L,
    val nickname: String = "",
    val colorId: Int = 0,
)