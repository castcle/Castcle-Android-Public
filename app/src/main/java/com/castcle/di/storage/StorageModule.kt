package com.castcle.di.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.castcle.data.storage.*
import dagger.*
import javax.inject.Singleton

@Module
abstract class StorageModule {

    @Binds
    @Singleton
    internal abstract fun appPreferences(appPreferences: AppPreferencesImpl): AppPreferences

    @Binds
    @Singleton
    internal abstract fun deviceSettings(deviceSettings: DeviceSettingsImpl): DeviceSettings

    companion object {

        @Provides
        @Singleton
        fun sharedPreferences(context: Context): SharedPreferences {
            return PreferenceManager.getDefaultSharedPreferences(context)
        }
    }
}
