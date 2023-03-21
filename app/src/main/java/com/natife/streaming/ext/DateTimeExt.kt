package com.natife.streaming.ext

import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.*
import java.util.concurrent.TimeUnit

///new
fun Date.dayOfWeek(lang: String): String {
    return SimpleDateFormat("EEEE", Locale(lang)).format(this)
}

fun Date.year(lang: String): String {
    return SimpleDateFormat("yyyy", Locale(lang)).format(this)
}

fun Date.toRequest(): String {
    val sdf0 = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
//    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
    val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
    return sdf.format(sdf0.parse(sdf0.format(this)))
}

fun String.fromResponse(): Date {
    val sdf = SimpleDateFormat("yyyy-MM-ddHH:mm:ss'Z'", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.parse(this)
}

//new
fun Date.toDisplay(lang: String): String {
    val sdf = SimpleDateFormat("dd MMMM", Locale(lang))
    return sdf.format(this)
}

fun Date.toDisplay3(lang: String): String {
    val sdf = SimpleDateFormat("HH:mm", Locale(lang))
    sdf.timeZone = TimeZone.getDefault()
    return sdf.format(this)
}

fun Date.toDisplayDate(lang: String): String {
    val sdf = SimpleDateFormat("dd.MM.yy", Locale(lang))
    return sdf.format(this)
}

fun Date.toDisplay2(): String {
    val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("ru"))//TODO multilang
    return sdf.format(this)
}

fun LocalDate.fromCalendar(): Date? {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.parse(this.toString())
}

fun Long.toDate(): Date {
    return Date(this)
}

fun Long.toDisplayTime() = when (val miliss = this * 1000) {
    in 0 until 1000 * 60 * 60 -> {
        String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(miliss) - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS.toHours(
                    miliss
                )
            ),
            TimeUnit.MILLISECONDS.toSeconds(miliss) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(
                    miliss
                )
            )
        )
    }
    else -> String.format(
        "%02d:%02d:%02d",
        TimeUnit.MILLISECONDS.toHours(miliss),
        TimeUnit.MILLISECONDS.toMinutes(miliss) - TimeUnit.HOURS.toMinutes(
            TimeUnit.MILLISECONDS.toHours(
                miliss
            )
        ),
        TimeUnit.MILLISECONDS.toSeconds(miliss) - TimeUnit.MINUTES.toSeconds(
            TimeUnit.MILLISECONDS.toMinutes(
                miliss
            )
        )
    )
}

fun daysOfWeekFromLocale(): Array<DayOfWeek> {
    val firstDayOfWeek = WeekFields.of(Locale("ru")).firstDayOfWeek
    var daysOfWeek = DayOfWeek.values()
//    Timber.e("TUTITYTUYTUYT ${firstDayOfWeek} ${daysOfWeek}")
    // Order `daysOfWeek` array so that firstDayOfWeek is at index 0.
    // Only necessary if firstDayOfWeek != DayOfWeek.MONDAY which has ordinal 0.
    if (firstDayOfWeek != DayOfWeek.MONDAY) {
        val rhs = daysOfWeek.sliceArray(firstDayOfWeek.ordinal..daysOfWeek.indices.last)
        val lhs = daysOfWeek.sliceArray(0 until firstDayOfWeek.ordinal)
        daysOfWeek = rhs + lhs
    }
    return daysOfWeek
}
