package com.natife.streaming.ext


class ImageBinder {
//    fun bindImage(item: Players, sport: Int, conteiner: ImageView?) {
//        var url = "http://empty"
//        if (sport == 1) {
//            //Футболл
//            url = "https://instatscout.com/images/players/180/" + item.getId().toString() + ".png"
//        } else if (sport == 2) {
//            //Хокей
//            url = "https://hockey.instatscout.com/images/players/180/" + item.getId()
//                .toString() + ".png"
//        } else if (sport == 3) {
//            //Баскетбол
//            url = "https://basketball.instatscout.com/images/players/180/" + item.getId()
//                .toString() + ".png"
//        }
//        Log.v("==APP::IMAGE_SPORT==", sport.toString())
//        Log.v("==APP::IMAGE_URL==", url)
//        val placeHolder: Int = R.drawable.ic_no_photo_player
//        Picasso.get()
//            .load(url)
//            .placeholder(placeHolder)
//            .error(placeHolder)
//            .into(conteiner)
//    }
//
//    fun bindImage(item: ItemModel, conteiner: ImageView?) {
//        var url = "http://empty"
//        if (item.getSport() === 1) {
//            //Футболл
//            if (item.isPlayer()) {
//                url =
//                    "https://instatscout.com/images/players/180/" + item.getId().toString() + ".png"
//            } else if (item.isTeam()) {
//                url = "https://instatscout.com/images/teams/180/" + item.getId().toString() + ".png"
//            } else if (item.isTournament()) {
//                url = "https://instatscout.com/images/tournaments/180/" + item.getId()
//                    .toString() + ".png"
//            }
//        } else if (item.getSport() === 2) {
//            //Хокей
//            if (item.isPlayer()) {
//                url = "https://hockey.instatscout.com/images/players/180/" + item.getId()
//                    .toString() + ".png"
//            } else if (item.isTeam()) {
//                url = "https://hockey.instatscout.com/images/teams/180/" + item.getId()
//                    .toString() + ".png"
//            } else if (item.isTournament()) {
//                url = "https://hockey.instatscout.com/images/tournaments/180/" + item.getId()
//                    .toString() + ".png"
//            }
//        } else if (item.getSport() === 3) {
//            //Баскетбол
//            if (item.isPlayer()) {
//                url = "https://basketball.instatscout.com/images/players/180/" + item.getId()
//                    .toString() + ".png"
//            } else if (item.isTeam()) {
//                url = "https://basketball.instatscout.com/images/teams/180/" + item.getId()
//                    .toString() + ".png"
//            } else if (item.isTournament()) {
//                url = "https://basketball.instatscout.com/images/tournaments/180/" + item.getId()
//                    .toString() + ".png"
//            }
//        }
//        Log.v("==APP::IMAGE_SPORT==", item.getSport().toString())
//        Log.v("==APP::IMAGE_URL==", url)
//        var placeHolder: Int = R.drawable.ic_account
//        if (item.isPlayer()) {
//            placeHolder = R.drawable.ic_no_photo_player
//        } else if (item.isTeam()) {
//            placeHolder = R.drawable.ic_no_photo_team
//        } else if (item.isTournament()) {
//            placeHolder = R.drawable.ic_no_photo_tournament
//        }
//        Picasso.get()
//            .load(url)
//            .placeholder(placeHolder)
//            .error(placeHolder)
//            .into(conteiner)
//    }
//
//    fun bindFlagImage(flagId: Int, conteiner: ImageView?) {
//        var url = "https://instatscout.com/images/flags/48/0.png"
//        if (flagId != -1) {
//            url = "https://instatscout.com/images/flags/48/$flagId.png"
//        }
//        val placeHolder: Int = R.drawable.ic_flag_icon
//        Picasso.get()
//            .load(url)
//            .placeholder(placeHolder)
//            .error(placeHolder)
//            .into(conteiner)
//    }
}