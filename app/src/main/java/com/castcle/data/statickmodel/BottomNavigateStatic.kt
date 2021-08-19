package com.castcle.data.statickmodel

import com.castcle.android.R
import com.castcle.data.model.BottomNavigation

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
//  Created by sklim on 19/8/2021 AD at 13:03.

object BottomNavigateStatic {
    val bottomMenu = mutableListOf(
        BottomNavigation(
            R.string.bottom_menu_feed,
            R.drawable.ic_feed_selector,
            R.id.onboard_nav_graph
        ),
        BottomNavigation(
            R.string.bottom_menu_create,
            R.drawable.ic_create_content,
            R.id.bloc_nav_graph
        ),
        BottomNavigation(
            R.string.bottom_menu_search,
            R.drawable.ic_search,
            R.id.search_nav_graph
        )
    )
}