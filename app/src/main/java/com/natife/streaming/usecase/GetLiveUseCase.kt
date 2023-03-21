package com.natife.streaming.usecase

interface GetLiveUseCase {
    suspend fun execute(): List<String>
}

class GetLiveUseCaseImpl : GetLiveUseCase {
    override suspend fun execute(): List<String> {
        return listOf("Live", "Finished", "Soon", "All")
    }
}

