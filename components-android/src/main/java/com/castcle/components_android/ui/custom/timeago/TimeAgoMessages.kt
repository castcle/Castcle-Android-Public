package com.castcle.components_android.ui.custom.timeago

import java.text.MessageFormat
import java.util.*


class TimeAgoMessages

private constructor() {

    private var bundle: ResourceBundle? = null

    fun getPropertyValue(property: String): String {
        return bundle!!.getString(property)
    }

    fun getPropertyValue(property: String, vararg values: Any): String {
        val propertyVal = getPropertyValue(property)
        return MessageFormat.format(propertyVal, *values)
    }

    class Builder {

        private var innerBundle: ResourceBundle? = null

        fun build(): TimeAgoMessages {
            val resources = TimeAgoMessages()
            resources.bundle = this.innerBundle
            return resources
        }

        fun defaultLocale(): Builder {
            this.innerBundle = ResourceBundle.getBundle(MESSAGES)
            return this
        }

        fun withLocale(locale: Locale): Builder {
            this.innerBundle = ResourceBundle.getBundle(MESSAGES, locale)
            return this
        }
    }

    companion object {

        private const val MESSAGES = "timeago.messages"
    }
}