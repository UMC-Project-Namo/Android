package com.mongmong.namo.data.repositoriyImpl

import com.mongmong.namo.data.datasource.profile.RemoteProfileDataSource
import com.mongmong.namo.data.utils.mappers.ProfileMapper.toModel
import com.mongmong.namo.domain.model.ProfileModel
import com.mongmong.namo.domain.repositories.ProfileRepository
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val remoteProfileDataSource: RemoteProfileDataSource
): ProfileRepository {

    override suspend fun getProfile(): ProfileModel {
        return remoteProfileDataSource.getProfile().result.toModel()
    }
}