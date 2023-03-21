package com.natife.streaming.usecase
@Deprecated("don't use")
interface SearchTypeUseCase {
    suspend fun execute(): List<String>
}

@Deprecated("don't use")
class SearchTypeUseCaseImpl: SearchTypeUseCase {
    override suspend fun execute(): List<String> {
        return listOf("Команда","Игрок","Турнир")
    }
}