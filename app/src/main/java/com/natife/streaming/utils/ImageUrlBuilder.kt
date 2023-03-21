package com.natife.streaming.utils

class ImageUrlBuilder {

    companion object{
        fun getUrl(sportId:Int,type:Type,id: Int): String{
            return "${getBaseUrl(sportId)}images/${getPath(type)}180/$id.png"
        }
        fun getPlaceholder(sportId:Int,type:Type): String{
            return "${getBaseUrlPlaceholder(sportId)}images/${getPathPlaceholder(type,sportId)}"
        }
        private fun getBaseUrl(sportId: Int): String{
            return when(sportId){
                1->"https://instatscout.com/"
                2->"https://hockey.instatscout.com/"
                3->"https://basketball.instatscout.com/"
                else ->""

            }

        }
        private fun getBaseUrlPlaceholder(sportId: Int): String{
            return when(sportId){
                1->"https://football.instatscout.com/"
                2->"https://hockey.instatscout.com/"
                3->"https://basketball.instatscout.com/"
                else ->""

            }

        }
        private fun getPath(type:Type): String{
            return when(type){
                Type.TOURNAMENT->"tournaments/"
                Type.TEAM->"teams/"
                Type.PLAYER->"players/"
            }

        }
        private fun getPathPlaceholder(type:Type,sportId: Int): String{
            return when(type){
                Type.TOURNAMENT -> if (sportId == 1) {
                    "tournament-no-photo.png"
                } else {
                    "tournaments/180/no-photo.png"
                }
                Type.TEAM -> "team_no_photo.png"
                Type.PLAYER -> "player-no-photo.png"
            }

        }
        enum class Type{
            TOURNAMENT,
            TEAM,
            PLAYER
        }

    }

}
