package com.castcle.ui.search.trend

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.castcle.ui.search.trend.childview.*

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
//  Created by sklim on 16/9/2021 AD at 19:13.

class ContentTrendAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return FRAGMENT_POSITION
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            ALL_FRAGMENT_POSITION -> TopFragment()
            POST_FRAGMENT_POSITION -> LastestFragment()
            BLOG_FRAGMENT_POSITION -> PeopleFragment()
            PHOTO_FRAGMENT_POSITION -> PhotoFragment()
            else -> throw IllegalArgumentException("Position exceeds ${FRAGMENT_POSITION - 1}")
        }
    }
}

private const val FRAGMENT_POSITION = 4

private const val ALL_FRAGMENT_POSITION = 0
private const val POST_FRAGMENT_POSITION = 1
private const val BLOG_FRAGMENT_POSITION = 2
private const val PHOTO_FRAGMENT_POSITION = 3
