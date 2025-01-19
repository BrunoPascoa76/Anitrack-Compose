package cm.project.anitrack_compose

import cm.project.anitrack_compose.graphql.type.MediaSeason
import java.time.LocalDate

fun fuzzyDateToString(day: Int?, month: Int?, year: Int?): String {
    if (day == null && month == null && year == null) return "N/A"
    return (day?.toString() ?: "??") + "/" + (month?.toString() ?: "??") + "/" + (year?.toString()
        ?: "????")
}

fun getSeason(seasonsFromNow: Int): Pair<MediaSeason, Int> {
    val date = LocalDate.now().plusMonths(3L * seasonsFromNow)
    return when (date.monthValue / 3) {
        0 -> Pair(MediaSeason.WINTER, date.year)
        1 -> Pair(MediaSeason.SPRING, date.year)
        2 -> Pair(MediaSeason.SUMMER, date.year)
        3 -> Pair(MediaSeason.FALL, date.year)
        else -> throw Exception("You somehow broke math, congrats")
    }
}