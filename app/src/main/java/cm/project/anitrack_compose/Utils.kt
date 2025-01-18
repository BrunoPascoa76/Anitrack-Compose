package cm.project.anitrack_compose

fun fuzzyDateToString(day: Int?, month: Int?, year: Int?): String {
    if (day == null && month == null && year == null) return "N/A"
    return (day?.toString() ?: "??") + "/" + (month?.toString() ?: "??") + "/" + (year?.toString()
        ?: "????")
}