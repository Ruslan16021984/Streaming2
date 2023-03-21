package com.natife.streaming.data.dto.preferences

import com.google.gson.annotations.SerializedName

data class SaveUserPreferencesResponse(
    @SerializedName("_p_status")
    val status: Int? = null, //  -- 1-OK, 2-ошибка
)
