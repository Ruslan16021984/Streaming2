package com.natife.streaming.usecase

interface GenderUseCase {
    suspend fun execute(): List<String>
}
class GenderUseCaseImpl:GenderUseCase{
    override suspend fun execute(): List<String> {
        return listOf("Мужчины","Женщины")
    }

}