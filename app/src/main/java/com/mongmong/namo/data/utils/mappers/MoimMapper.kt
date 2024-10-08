package com.mongmong.namo.data.utils.mappers

import com.mongmong.namo.data.dto.CalendarParticipant
import com.mongmong.namo.data.dto.GetMoimCalendarResult
import com.mongmong.namo.data.dto.GetMoimDetailResult
import com.mongmong.namo.data.dto.GetMoimResult
import com.mongmong.namo.data.dto.MoimParticipant
import com.mongmong.namo.data.dto.MoimScheduleRequestBody
import com.mongmong.namo.data.dto.Period
import com.mongmong.namo.data.dto.ScheduleLocation
import com.mongmong.namo.domain.model.Location
import com.mongmong.namo.domain.model.MoimCalendarParticipant
import com.mongmong.namo.domain.model.MoimCalendarSchedule
import com.mongmong.namo.domain.model.MoimPreview
import com.mongmong.namo.domain.model.MoimScheduleDetail
import com.mongmong.namo.domain.model.Participant

object MoimMapper {
    // DTO -> Model
    fun GetMoimResult.toModel(): MoimPreview {
        return MoimPreview(
            moimId = this.meetingScheduleId,
            startDate = this.startDate,
            coverImg = this.imageUrl,
            title = this.title,
            participantCount = this.participantCount,
            participantNicknames = this.participantNicknames
        )
    }

    fun GetMoimDetailResult.toModel(): MoimScheduleDetail {
        return MoimScheduleDetail(
            moimId = this.scheduleId,
            title = this.title,
            coverImg = this.imageUrl,
            startDate = this.startDate,
            endDate = this.endDate,
            locationInfo = Location(
                this.locationInfo.longitude,
                this.locationInfo.latitude,
                this.locationInfo.locationName,
                this.locationInfo.kakaoLocationId
            ),
            participants = this.participants.map { participantData ->
                participantData.toModel()
            }
        )
    }

    fun MoimParticipant.toModel(): Participant {
        return Participant(
            participantId = this.participantId,
            userId = this.userId,
            nickname = this.nickname,
            colorId = this.colorId,
            isGuest = this.isGuest,
            isOwner = this.isOwner
        )
    }

    fun GetMoimCalendarResult.toModel(): MoimCalendarSchedule {
        return MoimCalendarSchedule(
            scheduleId = this.scheduleId,
            title = this.title,
            startDate = this.startDate,
            endDate = this.endDate,
            participants = this.participants.map { participantData ->
                participantData.toModel()
            },
            isCurMoim = this.isCurMeetingSchedule
        )
    }

    fun CalendarParticipant.toModel(): MoimCalendarParticipant {
        return MoimCalendarParticipant(
            participantId = this.participantId,
            userId = this.userId,
            nickname = this.nickname,
            colorId = this.colorId
        )
    }

    fun MoimScheduleDetail.toModel(): MoimScheduleRequestBody {
        return MoimScheduleRequestBody(
            title = this.title,
            imageUrl = this.coverImg,
            period = Period(
                this.startDate,
                this.endDate
            ),
            location = ScheduleLocation(
                this.locationInfo.longitude,
                this.locationInfo.latitude,
                this.locationInfo.locationName,
                this.locationInfo.kakaoLocationId
            ),
            participants = this.participants.map { it.userId }
        )
    }
}