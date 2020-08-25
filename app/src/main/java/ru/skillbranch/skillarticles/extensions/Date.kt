package ru.skillbranch.skillarticles.extensions

import java.text.SimpleDateFormat
import java.util.*

const val SECOND = 1000L
const val MINUTE = 60 * SECOND
const val HOUR = 60 * MINUTE
const val DAY = 24 * HOUR

fun Date.format(pattern: String = "HH:mm:ss dd.MM.yy"): String {
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Date.shortFormat(): String {
    val pattern = if (this.isSameDay(Date())) "HH:mm" else "dd.MM.yy"
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Date.isSameDay(date: Date): Boolean {
    val day1 = this.time / DAY
    val day2 = date.time / DAY
    return day1 == day2
}

fun Date.add(value: Int, units: TimeUnits = TimeUnits.SECOND): Date {
    var time = this.time

    time += when (units) {
        TimeUnits.SECOND -> value * SECOND
        TimeUnits.MINUTE -> value * MINUTE
        TimeUnits.HOUR -> value * HOUR
        TimeUnits.DAY -> value * DAY
    }
    this.time = time
    return this
}

fun Date.humanizeDiff(date: Date = Date()): String {
    val currentDate = date.time
    val unixDate = this.time
    val exactTime = kotlin.math.abs(currentDate - unixDate)
    val result: String

    when {
        currentDate > unixDate + 1 * SECOND && currentDate < unixDate + 45 * SECOND ->
            result = "несколько секунд назад"
        currentDate < unixDate - 1 * SECOND && currentDate > unixDate - 45 * SECOND ->
            result = "через несколько секунд"

        currentDate > unixDate + 45 * SECOND && currentDate < unixDate + 75 * SECOND ->
            result = "минуту назад"
        currentDate < unixDate - 45 * SECOND && currentDate > unixDate - 75 * SECOND ->
            result = "через минуту"

        currentDate > unixDate + 75 * SECOND && currentDate < unixDate + 45 * MINUTE ->
            result = "${TimeUnits.MINUTE.plural((exactTime / MINUTE).toInt())} назад"
        currentDate < unixDate - 75 * SECOND && currentDate > unixDate - 45 * MINUTE ->
            result = "через ${TimeUnits.MINUTE.plural((exactTime / MINUTE).toInt())}"

        currentDate > unixDate + 45 * MINUTE && currentDate < unixDate + 75 * MINUTE ->
            result = "час назад"
        currentDate < unixDate - 45 * MINUTE && currentDate > unixDate - 75 * MINUTE ->
            result = "через час"

        currentDate > unixDate + 75 * MINUTE && currentDate < unixDate + 22 * HOUR ->
            result = "${TimeUnits.HOUR.plural((exactTime / HOUR).toInt())} назад"
        currentDate < unixDate - 75 * MINUTE && currentDate > unixDate - 22 * HOUR ->
            result = "через ${TimeUnits.HOUR.plural((exactTime / HOUR).toInt())}"

        currentDate > unixDate + 22 * HOUR && currentDate < unixDate + 26 * HOUR ->
            result = "день назад"
        currentDate < unixDate - 22 * HOUR && currentDate > unixDate - 26 * HOUR ->
            result = "через день"

        currentDate > unixDate + 26 * HOUR && currentDate < unixDate + 360 * DAY ->
            result = "${TimeUnits.DAY.plural((exactTime / DAY).toInt())} назад"
        currentDate < unixDate - 26 * HOUR && currentDate > unixDate - 360 * DAY ->
            result = "через ${TimeUnits.DAY.plural((exactTime / DAY).toInt())}"

        currentDate > unixDate + 360 * DAY ->
            result = "более года назад"
        currentDate < unixDate - 360 * DAY ->
            result = "более чем через год"

        else -> result = "только что"
    }
    return result
}

enum class TimeUnits {
    SECOND {
        override fun plural(value: Int): String {
            val valueString: String = value.toString()
            return if (value in 5..20) {
                "$value секунд"
            } else {
                when {
                    valueString.takeLast(1) == "1" -> "$value секунду"
                    valueString.takeLast(1) > "1" && valueString.takeLast(1) < "5" -> "$value секунды"
                    else -> "$value секунд"
                }
            }
        }
    },

    MINUTE {
        override fun plural(value: Int): String {
            val valueString: String = value.toString()
            return if (value in 5..20) {
                "$value минут"
            } else {
                when {
                    valueString.takeLast(1) == "1" -> "$value минуту"
                    valueString.takeLast(1) > "1" && valueString.takeLast(1) < "5" -> "$value минуты"
                    else -> "$value минут"
                }
            }
        }
    },

    HOUR {
        override fun plural(value: Int): String {
            val valueString: String = value.toString()
            return if (value in 5..20) {
                "$value часов"
            } else {
                when {
                    valueString.takeLast(1) == "1" -> "$value час"
                    valueString.takeLast(1) > "1" && valueString.takeLast(1) < "5" -> "$value часа"
                    else -> "$value часов"
                }
            }
        }
    },

    DAY {
        override fun plural(value: Int): String {
            val valueString: String = value.toString()
            return if (value in 5..20) {
                "$value дней"
            } else {
                when {
                    valueString.takeLast(1) == "1" -> "$value день"
                    valueString.takeLast(1) > "1" && valueString.takeLast(1) < "5" -> "$value дня"
                    else -> "$value дней"
                }
            }
        }
    };

    abstract fun plural(value: Int): String
}