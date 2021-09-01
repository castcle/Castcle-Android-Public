package com.castcle.di.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.castcle.data.storage.*
import com.castcle.di.component.DaoModule
import dagger.*
import javax.inject.Singleton

@Module(includes = [DaoModule::class])
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

        @Provides
        @Singleton
        fun castcleDatabase(context: Context): CastcleDataBase {
            return getCastcleDatabase(context)
        }

        private fun getCastcleDatabase(context: Context): CastcleDataBase {
            return Room.databaseBuilder(context, CastcleDataBase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
