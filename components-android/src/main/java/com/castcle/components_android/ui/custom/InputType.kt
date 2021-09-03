package com.castcle.components_android.ui.custom

import android.text.InputType.*

enum class InputType {
    TEXT {
        override fun get() = TYPE_CLASS_TEXT
    },
    TEXT_EMAIL_ADDRESS {
        override fun get() = TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_EMAIL_ADDRESS
    },
    TEXT_PASSWORD {
        override fun get() = TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_PASSWORD
    },
    TEXT_CAP_SENTENCES {
        override fun get() = TYPE_CLASS_TEXT or TYPE_TEXT_FLAG_CAP_SENTENCES
    },
    NUMBER_DECIMAL {
        override fun get() = TYPE_CLASS_NUMBER or TYPE_NUMBER_FLAG_DECIMAL
    },
    NUMBER_PASSWORD {
        override fun get() = TYPE_CLASS_NUMBER or TYPE_NUMBER_VARIATION_PASSWORD
    },
    NUMBER_PHONE {
        override fun get() = TYPE_CLASS_TEXT or TYPE_CLASS_PHONE
    },
    NUMBER {
        override fun get() = TYPE_CLASS_NUMBER
    };

    abstract fun get(): Int

    companion object {
        fun from(index: Int) = values()[index]
    }
}
