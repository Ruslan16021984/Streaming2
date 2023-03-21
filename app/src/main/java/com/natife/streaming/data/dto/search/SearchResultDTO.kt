package com.natife.streaming.data.dto.search

data class SearchResultDTO(
    val players: List<PlayerDTO>?,
    val teams: List<TeamTournamentDTO>?,
    val tournaments: List<TeamTournamentDTO>?
)