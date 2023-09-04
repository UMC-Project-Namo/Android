package com.example.namo.data.remote.diary

import com.example.namo.config.BaseResponse
import com.google.gson.annotations.SerializedName

class DiaryResponse {

    data class DiaryResponse(
        val result: String
    ) : BaseResponse() // 기본 string


    /** 기록 추가 **/
    data class DiaryAddResponse(
        val result: GetScheduleIdx
    ) : BaseResponse()

    data class GetScheduleIdx(
        val scheduleIdx: Long
    )

    /** 기록 월 별 조회 **/
    data class DiaryGetAllResponse(
        val result: List<Result>
    ) : BaseResponse()

    data class Result(
        val scheduleId: Long,
        val contents: String?,
        val urls: List<String>,
    )


    /** 모임 기록 개별 조회 **/
    data class GetGroupDiaryResponse(
        val result: GroupDiaryResult
    ) : BaseResponse()

    data class GroupDiaryResult(
        val startDate: Long,
        val locationName: String,
        val users: List<GroupUser>,
        val locationDtos: List<LocationDto>
    )

    data class GroupUser(
        val userId: Long,
        val userName: String
    ) : java.io.Serializable

    data class LocationDto(
        val moimMemoLocationId: Long,
        @SerializedName("name") var place: String,
        @SerializedName("money") var pay: Long,
        @SerializedName("participants") var members: MutableList<Long>,
        @SerializedName("urls") var imgs: List<String>
    ) : java.io.Serializable

}

