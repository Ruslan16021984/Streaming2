package com.natife.streaming.usecase

@Deprecated("localSqlDataSourse.getGlobalSettings()")
interface GetShowScoreUseCase {
    suspend fun execute(): List<String>
}

@Deprecated("localSqlDataSourse.getGlobalSettings()")
class GetShowScoreUseCaseImpl: GetShowScoreUseCase{
    override suspend fun execute(): List<String> {
        //TODO need repository
        return listOf("Да","Нет")
    }

}