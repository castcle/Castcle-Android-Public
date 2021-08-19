package com.castcle.data.storage

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import javax.inject.Inject

interface DeviceSettings {
    val deviceId: String
    val deviceName: String
}

class DeviceSettingsImpl @Inject constructor(
    private val context: Context
) : DeviceSettings {

    override val deviceId: String
        @SuppressLint("HardwareIds")
        get() = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )

    override val deviceName: String
        get() = Build.MODEL
}
