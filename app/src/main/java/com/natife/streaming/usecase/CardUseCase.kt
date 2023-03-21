package com.natife.streaming.usecase

import com.natife.streaming.data.Card

interface CardUseCase {
    suspend fun execute(): List<Card>
}
class CardUseCaseImpl : CardUseCase{
    override suspend fun execute(): List<Card> {
        return listOf(
            Card(1,"card 1","2909900032329090"),
            Card(2,"card 2","2909900032329090"),
            Card(3,"card 3","2909900032329090"))
    }
}