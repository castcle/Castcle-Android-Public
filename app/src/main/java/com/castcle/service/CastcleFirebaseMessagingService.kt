package com.castcle.service

import android.annotation.SuppressLint
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.castcle.android.BuildConfig
import com.castcle.service.notification.NotificationManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber
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
//  Created by sklim on 26/10/2021 AD at 19:37.

class CastcleFirebaseMessagingService : FirebaseMessagingService() {

    @Inject lateinit var notificationManager: NotificationManager

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        notificationManager.showNotification(message)
        sendLocalBroadCast()
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        Timber.i("====> Push notification token: $newToken")
    }

    private fun sendLocalBroadCast() {
        val newNotificationIntent = Intent(ACTION_NEW_PUSH_NOTIFICATION)
        LocalBroadcastManager.getInstance(this).sendBroadcast(newNotificationIntent)
    }
}

const val ACTION_NEW_PUSH_NOTIFICATION = "${BuildConfig.APPLICATION_ID}.NEW_PUSH_NOTIFICATION"
