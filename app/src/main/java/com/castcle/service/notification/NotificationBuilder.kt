package com.castcle.service.notification

import android.app.Notification
import android.content.Context
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.castcle.android.BuildConfig
import com.castcle.android.R
import com.google.firebase.messaging.RemoteMessage
import java.net.URL
import javax.inject.Inject
import kotlin.random.Random

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
//  Created by sklim on 26/10/2021 AD at 20:16.

interface NotificationBuilder {
    fun build(remoteMessage: RemoteMessage, channelId: String): Notification

    fun buildGroup(remoteMessage: RemoteMessage, channelId: String): Notification
}

class NotificationBuilderImpl @Inject constructor(
    private val appContext: Context
) : NotificationBuilder {
    override fun build(remoteMessage: RemoteMessage, channelId: String): Notification {
        val title = remoteMessage.data[DATA_TITLE] ?: remoteMessage.notification?.title
        val body = remoteMessage.data[DATA_BODY] ?: remoteMessage.notification?.body
        val imageUrl = remoteMessage.data[DATA_IMAGE]
            ?: remoteMessage.notification?.imageUrl?.toString()
        val uri = remoteMessage.data[DATA_URI] ?: remoteMessage.notification?.link?.toString()
        val image = imageUrl?.let {
            BitmapFactory.decodeStream(
                URL(it).openConnection().getInputStream()
            )
        }

        val pushData = Bundle()
        val keySet: Set<String> = remoteMessage.data.keys
        for (key in keySet) {
            pushData.putString(key, remoteMessage.data[key])
        }
        val distinctRequestCode = Random.nextInt(RANDOM_REQUEST_CODE_MIN, RANDOM_REQUEST_CODE_MAX)
        return NotificationCompat.Builder(appContext, channelId)
            .setSmallIcon(R.drawable.ic_logo_castcle_noti)
            .setLargeIcon(image)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setGroup(GENERAL_GROUP_KEY)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setAutoCancel(true)
            .setStyle(
                when (image) {
                    null -> NotificationCompat.BigTextStyle().bigText(body)
                    else -> NotificationCompat.BigPictureStyle()
                        .bigPicture(image)
                        .bigLargeIcon(null)
                        .setSummaryText(body)
                }
            )
            .build()
    }

    override fun buildGroup(remoteMessage: RemoteMessage, channelId: String): Notification {
        return NotificationCompat.Builder(appContext, channelId)
            .setSmallIcon(R.drawable.ic_logo_castcle_noti)
            .setGroup(GENERAL_GROUP_KEY)
            .setContentTitle(appContext.getString(R.string.app_name))
            .setGroupSummary(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
    }
}

private const val GENERAL_GROUP_KEY = "${BuildConfig.APPLICATION_ID}.notification.general_group"
private const val DATA_TITLE = "title"
private const val DATA_BODY = "body"
private const val DATA_IMAGE = "media-attachment-url"
private const val DATA_URI = "uri"
const val EXTRA_IS_REMOTE_PUSH_NOTIFICATION = "RemotePushNotification"

private const val RANDOM_REQUEST_CODE_MIN = 0
private const val RANDOM_REQUEST_CODE_MAX = 1000
