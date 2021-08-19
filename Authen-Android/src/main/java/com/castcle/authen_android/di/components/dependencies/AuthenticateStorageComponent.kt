package com.castcle.authen_android.di.components.dependencies

import android.content.SharedPreferences

interface AuthenticateStorageComponent {
    fun sharedPreferences(): SharedPreferences
}
