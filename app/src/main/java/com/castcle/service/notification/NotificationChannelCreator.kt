package com.castcle.service.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.castcle.android.R
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
//  Created by sklim on 26/10/2021 AD at 20:34.

interface NotificationChannelCreator {
    fun create(channelId: String)
}

class NotificationChannelCreatorImpl @Inject constructor(
    private val appContext: Context
) : NotificationChannelCreator {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun create(channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = appContext.getString(R.string.general_notification_channel_name)
            val description =
                appContext.getString(R.string.general_notification_channel_description)

            appContext
                .getSystemService(NotificationManager::class.java)
                ?.createNotificationChannel(
                    NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_DEFAULT)
                        .also { it.description = description }
                )
        }
    }
}
