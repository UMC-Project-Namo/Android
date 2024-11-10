package com.mongmong.namo.presentation.utils

import com.mongmong.namo.domain.model.Participant

object StringConverter {
    @JvmStatic
    fun getMembersText(memberList: List<Participant>): String {
        if (memberList.isEmpty()) return "없음"
        return memberList.joinToString(", ") { it.nickname }
    }
}