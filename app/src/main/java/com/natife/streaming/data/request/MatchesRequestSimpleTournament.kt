package com.natife.streaming.data.request

import com.google.gson.annotations.SerializedName
import com.natife.streaming.ext.toRequest
import java.util.*

interface MatchRequest

data class MatchesRequestSimpleTournament(
    @SerializedName( "_p_date")
    val date: String? = null,
    @SerializedName( "_p_limit")
    val limit: Int = 60,
    @SerializedName( "_p_offset")
    val offset: Int = 0,
    @SerializedName( "_p_sport")
    val sport: Int? = null,
    @SerializedName( "_p_sub_only")
    val subOnly: Boolean = true,
    @SerializedName( "_p_tournament_id")
    val tournamentId: Int? = null
):MatchRequest
data class PlayerMatchesRequest(
    @SerializedName( "_p_date")
    val date: String? = null,
    @SerializedName( "_p_limit")
    val limit: Int = 60,
    @SerializedName( "_p_offset")
    val offset: Int = 0,
    @SerializedName( "_p_sport")
    val sport: Int? = null,
    @SerializedName( "_p_sub_only")
    val subOnly: Boolean = true,
    @SerializedName( "_p_player_id")
    val playerId: Int? = null
):MatchRequest
data class TeamMatchesRequest(
    @SerializedName( "_p_date")
    val date: String? = null,
    @SerializedName( "_p_limit")
    val limit: Int = 60,
    @SerializedName( "_p_offset")
    val offset: Int = 0,
    @SerializedName( "_p_sport")
    val sport: Int? = null,
    @SerializedName( "_p_sub_only")
    val subOnly: Boolean = true,
    @SerializedName( "_p_team_id")
    val teamId: Int? = null
):MatchRequest
