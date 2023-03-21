package com.natife.streaming.usecase

import com.natife.streaming.api.MainApi
import com.natife.streaming.data.dto.matchprofile.ProfileColorDTO
import com.natife.streaming.data.request.ProfileColorRequest


interface ProfileColorUseCase {
    suspend fun execute(sportId: Int, profileType: String, profileId: Int): String
}

class ProfileColorUseCaseImpl(private val api: MainApi) : ProfileColorUseCase {
    override suspend fun execute(
        sportId: Int,
        profileType: String,
        profileId: Int
    ): String {
        val color: ProfileColorDTO = api.getProfileColor(
            body = ProfileColorRequest(
                sportId = sportId,
                profileId = profileId,
                profileType = profileType
            )
        )

        return color.code.toString()
    }
}


