package com.mongmong.namo.domain.repositories

import com.mongmong.namo.data.dto.TermBody

interface TermRepository {
    suspend fun postTerms(
        termBody: TermBody
    ): Boolean
}