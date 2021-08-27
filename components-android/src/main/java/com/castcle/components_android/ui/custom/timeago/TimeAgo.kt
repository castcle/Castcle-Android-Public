package com.castcle.components_android.ui.custom.timeago

import kotlin.math.abs
import kotlin.math.roundToLong

class TimeAgo
private constructor() {

    companion object {
        @JvmStatic
        @JvmOverloads
        fun using(
            time: Long,
            resources: TimeAgoMessages = TimeAgoMessages.Builder().defaultLocale().build()
        ): String {
            val dim = getTimeDistanceInMinutes(time)
            val timeAgo = buildTimeagoText(resources, dim)
            return timeAgo.toString()
        }

        private fun buildTimeagoText(resources: TimeAgoMessages, dim: Long): StringBuilder {
            val timeAgo = StringBuilder()

            val foundTimePeriod = Periods.findByDistanceMinutes(dim)
            if (foundTimePeriod != null) {
                val periodKey = foundTimePeriod.propertyKey
                when (foundTimePeriod) {
                    Periods.X_MINUTES_PAST -> timeAgo.append(
                        resources.getPropertyValue(
                            periodKey,
                            dim
                        )
                    )
                    Periods.X_HOURS_PAST -> {
                        val hours = (dim / 60f).roundToLong()
                        val xHoursText = handlePeriodKeyAsPlural(
                            resources,
                            "timeago.aboutanhour.past", periodKey, hours.toInt()
                        )
                        timeAgo.append(xHoursText)
                    }
                    Periods.X_DAYS_PAST -> {
                        val days = (dim / 1440f).roundToLong()
                        val xDaysText = handlePeriodKeyAsPlural(
                            resources,
                            "timeago.oneday.past", periodKey, days.toInt()
                        )
                        timeAgo.append(xDaysText)
                    }
                    Periods.X_MINUTES_FUTURE -> timeAgo.append(
                        resources.getPropertyValue(
                            periodKey,
                            abs(dim.toFloat())
                        )
                    )
                    Periods.X_HOURS_FUTURE -> {
                        val hours1 = abs((dim / 60f).roundToLong())
                        val yHoursText = if (hours1.toInt() == 24)
                            resources.getPropertyValue("timeago.oneday.future")
                        else
                            handlePeriodKeyAsPlural(
                                resources, "timeago.aboutanhour.future",
                                periodKey, hours1.toInt()
                            )
                        timeAgo.append(yHoursText)
                    }
                    Periods.X_DAYS_FUTURE -> {
                        val days1 = abs((dim / 1440f).roundToLong())
                        val yDaysText = handlePeriodKeyAsPlural(
                            resources,
                            "timeago.oneday.future", periodKey, days1.toInt()
                        )
                        timeAgo.append(yDaysText)
                    }
                    else -> timeAgo.append(resources.getPropertyValue(periodKey))
                }
            }
            return timeAgo
        }

        private fun handlePeriodKeyAsPlural(
            resources: TimeAgoMessages,
            periodKey: String,
            pluralKey: String, value: Int
        ): String =
            if (value == 1) resources.getPropertyValue(periodKey) else resources.getPropertyValue(
                pluralKey,
                value
            )

        private fun getTimeDistanceInMinutes(time: Long): Long {
            val timeDistance = System.currentTimeMillis() - time
            return (timeDistance / 1000 / 60).toFloat().roundToLong()
        }
    }
}
