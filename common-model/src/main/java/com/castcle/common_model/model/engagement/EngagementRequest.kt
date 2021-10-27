package com.castcle.common_model.model.engagement

import com.google.gson.annotations.SerializedName

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
//  Created by sklim on 26/10/2021 AD at 11:48.

data class EngagementRequest(
    @SerializedName("accountId")
    var accountId: String = "",
    @SerializedName("client")
    var client: String = android.os.Build.MODEL,
    @SerializedName("eventData")
    var eventData: String? = null,
    @SerializedName("eventType")
    var eventType: String,
    @SerializedName("feedItemId")
    var feedItemId: String? = null,
    @SerializedName("platform")
    var platform: String = PLAT_FORM_DEFAULT,
    @SerializedName("screenId")
    var screenId: String,
    @SerializedName("screenInstance")
    var screenInstance: String? = null,
    @SerializedName("target")
    var target: String? = null,
    @SerializedName("targetId")
    var targetId: String? = null,
    @SerializedName("timestamp")
    var timestamp: String,
    @SerializedName("uxSessionId")
    var uxSessionId: String = "",
    var onStartSession: Boolean = false
)

private const val PLAT_FORM_DEFAULT = "andorid"
