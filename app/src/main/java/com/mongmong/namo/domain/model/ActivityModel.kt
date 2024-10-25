package com.mongmong.namo.domain.model

import com.mongmong.namo.presentation.config.BaseResponse
import java.math.BigDecimal

data class Activity(
    var endDate: String,
    val activityId: Long,
    var location: ActivityLocation,
    var participants: List<ActivityParticipant>,
    var startDate: String,
    var title: String,
    var tag: String,
    var payment: ActivityPayment,
    var images: List<DiaryImage>
)


data class ActivityLocation(
    val kakaoLocationId: String = "",
    val latitude: Double = 0.0,
    val locationName: String = "",
    val longitude: Double = 0.0
)

data class ActivityParticipant(
    val participantId: Long,
    val activityParticipantId: Long,
    val nickname: String
)

data class ActivityPayment(
    var totalAmount: BigDecimal = BigDecimal.ZERO,
    var divisionCount: Int = 0,
    var amountPerPerson: BigDecimal = BigDecimal.ZERO,
    var participants: List<PaymentParticipant>
)

data class PaymentParticipant(
    val id: Long = 0,
    val nickname: String = "",
    var isPayer: Boolean = false
)

data class DiaryBaseResponse(
    val result: String = "",
    val code: Int = 0,
    val message: String = "",
    val isSuccess: Boolean = false
)
