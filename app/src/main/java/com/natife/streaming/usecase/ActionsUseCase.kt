package com.natife.streaming.usecase

import com.natife.streaming.API_ACTIONS
import com.natife.streaming.API_TRANSLATE
import com.natife.streaming.api.MainApi
import com.natife.streaming.data.actions.Action
import com.natife.streaming.data.request.BaseRequest
import com.natife.streaming.data.request.EmptyRequest
import com.natife.streaming.data.request.TranslateRequest
import com.natife.streaming.db.dao.ActionDao
import com.natife.streaming.db.entity.ActionEntity

interface ActionsUseCase {
    suspend fun execute(sportId: Int): List<Action>
    suspend fun save(sportId: Int,actions: List<Action>)
}

class ActionsUseCaseImpl(private val api: MainApi,private val dao: ActionDao) : ActionsUseCase {
    override suspend fun execute(sportId: Int): List<Action> {
        val actions = api.getActions(
            BaseRequest(
                procedure = API_ACTIONS,
                params = EmptyRequest()
            ),
            MatchProfileUseCase.getPath(sportId)
        )
        val translate = api.getTranslate(
            BaseRequest(
                procedure = API_TRANSLATE,
                params = TranslateRequest("ru",//TODO multilang
                    params = actions.data.map { it.lexic })
            )
        )

        val result = mutableListOf<Action>()

        val entities = dao.getActions(sportId)
        actions.data.forEach { dataDTO ->
            result.add(
                Action(
                    id = dataDTO.id,
                    name = translate[dataDTO.lexic.toString()]?.text ?: "",
                    selected = entities.find { it.id == dataDTO.id }?.selected?:false
                    )
            )
        }
        return result

    }

    override suspend fun save(sportId: Int, actions: List<Action>) {
        dao.insertActions(actions = actions.map {
            ActionEntity(
                id = it.id,
                sportId = sportId,
                selected = it.selected
            )
        })
    }

}