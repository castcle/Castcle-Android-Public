package com.castcle.service.notification

import android.content.Context
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import com.castcle.android.BuildConfig
import com.google.firebase.messaging.RemoteMessage
import java.util.*
import javax.inject.Inject

//  Copyright (c) 2021, Castcle and/or its affiliates. All rights reserved.
//  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
//
//  This code is free software; you can redistribute it and/or modify it
//  under the terms of the GNU General Public License version 3 only, as
//  published by the Free Software Foundation.
//
//  This code is distributed in the hope that it will be useful, but WITHOUT
//  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
//  FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
//  version 3 for more details (a copy is included in the LICENSE file that
//  accompanied this code).
//
//  You should have received a copy of the GNU General Public License version
//  3 along with this work; if not, write to the Free Software Foundation,
//  Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
//
//  Please contact Castcle, 22 Phet Kasem 47/2 Alley, Bang Khae, Bangkok,
//  Thailand 10160, or visit www.castcle.com if you need additional information
//  or have any questions.
//
//
//  Created by sklim on 26/10/2021 AD at 19:58.

interface NotificationManager {
    fun showNotification(message: RemoteMessage)
}

class NotificationManagerImpl @Inject constructor(
    private val appContext: Context,
    private val notificationBuilder: NotificationBuilder,
    private val notificationChannelCreator: NotificationChannelCreator
) : NotificationManager {

    override fun showNotification(message: RemoteMessage) {
        val groupNotification = notificationBuilder.buildGroup(message, GENERAL_CHANNEL_ID)
        val notification = notificationBuilder.build(message, GENERAL_CHANNEL_ID)

        // Create the channel
        notificationChannelCreator.create(GENERAL_CHANNEL_ID)

        // Notify message
        NotificationManagerCompat
            .from(appContext)
            .run {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notify(GROUP_ID, groupNotification)
                }
                notify(GROUP_ID + Date().hashCode(), notification)
            }
    }
}

private const val GROUP_ID = 0
private const val GENERAL_CHANNEL_ID = "${BuildConfig.APPLICATION_ID}.notification.general_channel"