package com.mongmong.namo.domain.repositories

import com.mongmong.namo.domain.model.ProfileModel

interface ProfileRepository {
    suspend fun getProfile(): ProfileModel
}