package com.natife.streaming.ext

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.natife.streaming.R

fun ImageView.url(url: String) {
    Glide.with(this)
        .load(url)
        .override(this.width, this.height)
        .into(this)
}

fun ImageView.url(url: String, placeholder: String) {
    Glide.with(this)
        .load(url)
        .override(this.width, this.height)
        .error(Glide.with(this).load(placeholder).override(this.width, this.height))
        .into(this)
}

fun ImageView.urlCircled(url: String, placeholder: String) {
    Glide.with(this)
        .load(url)
        .override(this.width, this.height)
        .circleCrop()
        .error(Glide.with(this).load(placeholder).circleCrop().override(this.width, this.height))
        .into(this)
}


fun ImageView.bindFlagImage(flagId: Int) {
    var url = "https://instatscout.com/images/flags/48/0.png"
    if (flagId != -1) {
        url = "https://instatscout.com/images/flags/48/$flagId.png"
    }
    val placeHolder: Int = R.drawable.ic_flag_icon
    Glide.with(this)
        .load(url)
        .override(this.width, this.height)
        .error(placeHolder)
        .into(this)
}

fun ImageView.bindSportImage(sport: Int) {
    val img = when (sport) {
        1 -> R.drawable.ic_footbool_new //Футболл
        2 -> R.drawable.ic_hockey_new //Хокей
        3 -> R.drawable.ic_basketball_new //Баскетбол
        else -> R.drawable.ic_flag_icon
    }
    Glide.with(this)
        .load(img)
        .override(this.width, this.height)
        .into(this)
}

fun ImageView.bindTeamImage(sport: Int, teamId: Int) {
    val (url, errorPlaseHolder) = when (sport) {
        1 -> ("https://instatscout.com/images/teams/180/$teamId.png" to R.drawable.team_no_photo) //Футболл
        2 -> ("https://hockey.instatscout.com/images/teams/180/$teamId.png" to R.drawable.team_no_photo)//Хокей
        3 -> ("https://basketball.instatscout.com/images/teams/180/$teamId.png" to R.drawable.team_no_photo)//Баскетбол
        else -> {
            ("http://empty" to 0)
        }
    }
    Glide.with(this)
        .load(url)
        .override(this.width, this.height)
        .error(errorPlaseHolder)
        .into(this)
}