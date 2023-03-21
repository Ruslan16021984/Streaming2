package com.natife.streaming.data

data class Lexic(  val id:Int,
                   val lexisLangId: String,
                   val lang: String,
                   val text: String) {

    companion object {
        val emptyLexic = Lexic(0,"","","")
    }
}