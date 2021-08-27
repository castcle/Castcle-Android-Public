/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.castcle.networking.service.interceptor

import android.content.Context
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.core.FlipperClient
import com.facebook.flipper.core.FlipperPlugin
import com.facebook.flipper.plugins.crashreporter.CrashReporterPlugin
import com.facebook.flipper.plugins.databases.DatabasesFlipperPlugin
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.flipper.plugins.navigation.NavigationFlipperPlugin
import com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.facebook.flipper.plugins.sharedpreferences.SharedPreferencesFlipperPlugin
import okhttp3.OkHttpClient

/**
 * Extensible mobile app debugger
 * Flipper is a platform for debugging iOS, Android and React Native apps.
 * Visualize, inspect, and control your apps from a simple desktop interface.
 * Use Flipper as is or extend it using the plugin API.
 *
 * Ref: https://fbflipper.com/
 */
object FlipperInitializer {
    var initialized: Boolean = true

    fun initFlipperPlugins(
        context: Context,
        okHttpClient: OkHttpClient.Builder
    ) {
        if (initialized) {
            val client: FlipperClient = AndroidFlipperClient.getInstance(context)
            val descriptorMapping: DescriptorMapping = DescriptorMapping.withDefaults()
            val networkPlugin = NetworkFlipperPlugin()
            val networkInterceptor = FlipperOkhttpInterceptor(networkPlugin, true)
            okHttpClient.addNetworkInterceptor(networkInterceptor)

            client.addPlugin(InspectorFlipperPlugin(context, descriptorMapping))
            client.addPlugin(networkPlugin)
            client.addPlugin(getSharedPreferences(context))
            client.addPlugin(CrashReporterPlugin.getInstance())
            client.addPlugin(DatabasesFlipperPlugin(context))
            client.addPlugin(NavigationFlipperPlugin.getInstance())
            client.start()

            initialized = false
        }
    }

    private fun getSharedPreferences(
        context: Context
    ): FlipperPlugin {
        val sharedPreference = "${context.packageName}_preferences"
        return SharedPreferencesFlipperPlugin(
            context, listOf(
                SharedPreferencesFlipperPlugin.SharedPreferencesDescriptor(
                    sharedPreference,
                    Context.MODE_PRIVATE
                )
            )
        )
    }
}
