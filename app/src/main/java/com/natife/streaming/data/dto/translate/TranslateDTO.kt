package com.natife.streaming.data.dto.translate

import com.google.gson.annotations.SerializedName

data class TranslateDTO(
    val id: String ="",
    val lang: String ="",
    @SerializedName("lexis_lang_id")
    val lexisLangId: String ="",
    val text: String =""
)