package com.natife.streaming.data.dto.preferences

import com.google.gson.annotations.SerializedName

data class SaveUserPreferencesRequest2(
    @SerializedName(" _p_user_id")
    val userId: Int,
    @SerializedName("_p_data")
    val listOfPreferences: List<UserPreferencesDTO>?, //для удаления всего слать явно _p_data = null
)

