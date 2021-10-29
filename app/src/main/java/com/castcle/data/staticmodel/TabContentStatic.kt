package com.castcle.data.staticmodel

import com.castcle.android.R
import com.castcle.common_model.model.userprofile.TabContentUiModel

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
//  Created by sklim on 13/9/2021 AD at 21:53.

object TabContentStatic {
    val tabContent = mutableListOf(
        TabContentUiModel(
            tabName = "",
            tabNameRes = R.string.profile_tab_all,
            featureSlug = "all"
        ),
        TabContentUiModel(
            tabName = "",
            tabNameRes = R.string.profile_tab_post,
            featureSlug = "post"
        ),
        TabContentUiModel(
            tabName = "",
            tabNameRes = R.string.profile_tab_blog,
            featureSlug = "blog"
        ),
        TabContentUiModel(
            tabName = "",
            tabNameRes = R.string.profile_tab_photo,
            featureSlug = "photo"
        )
    )

    val tabTrend = mutableListOf(
        TabContentUiModel(
            tabName = "",
            tabNameRes = R.string.trend_tab_top,
            featureSlug = "top"
        ),
        TabContentUiModel(
            tabName = "",
            tabNameRes = R.string.trend_tab_lastest,
            featureSlug = "lastest"
        ),
        TabContentUiModel(
            tabName = "",
            tabNameRes = R.string.trend_tab_people,
            featureSlug = "people"
        ),
        TabContentUiModel(
            tabName = "",
            tabNameRes = R.string.trend_tab_photo,
            featureSlug = "photo"
        )
    )

    val tabNotification = mutableListOf(
        TabContentUiModel(
            tabNameRes = R.string.notification_tab_profile,
        ),
        TabContentUiModel(
            tabNameRes = R.string.notification_tab_page,
        ),
        TabContentUiModel(
            tabNameRes = R.string.notification_tab_system,
        )
    )
}