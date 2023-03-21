package com.natife.streaming.usecase

import com.natife.streaming.data.Subscription

interface SubscriptionUseCase {
    suspend fun execute(): List<Subscription>
    suspend fun executeAdditional(): List<Subscription>
}
class SubscriptionUseCaseImpl: SubscriptionUseCase {
    override suspend fun execute(): List<Subscription> {
      return listOf(Subscription(id = 1,"Бесплатная",0.0,true),Subscription(id = 2,"Базовая",0.0,true))
    }

    override suspend fun executeAdditional(): List<Subscription> {
        return listOf(Subscription(id = 3,"JPOKPOK",1.0,true),Subscription(id = 4,"MPMPKMOKMPOK",1000.0,false))
    }
}