package com.castcle.data.staticmodel

import com.castcle.android.R
import com.castcle.common_model.model.setting.*

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
//  Created by sklim on 7/9/2021 AD at 16:07.

object StaticSeetingMenu {
    val staticMenuSetting = listOf(
        SettingMenuUiModel(
            header = R.string.setting_menu_header_account,
            menuItem = listOf(
                MenuItem(
                    menuName = R.string.setting_account,
                    icon = R.drawable.ic_avata_profile,
                    menuType = SettingMenuType.PROFILE
                ),
                MenuItem(
                    menuName = R.string.setting_menu_privacy,
                    icon = R.drawable.ic_policry,
                    menuType = SettingMenuType.PRIVACY
                )
            )
        ),
        SettingMenuUiModel(
            header = R.string.setting_menu_header_language,
            menuItem = listOf(
                MenuItem(
                    menuName = R.string.setting_menu_language,
                    icon = R.drawable.ic_language,
                    menuType = SettingMenuType.LANGUAGE
                )
            )
        ),
        SettingMenuUiModel(
            header = 0,
            menuItem = listOf(
                MenuItem(
                    menuName = R.string.setting_menu_about_us,
                    icon = R.drawable.ic_info,
                    menuType = SettingMenuType.ABOUT_US
                )
            )
        )
    )

    val staticProfileSetting = listOf(
        SettingMenuUiModel(
            header = R.string.setting_account_section_account_setting,
            menuItem = listOf(
                MenuItem(
                    menuName = R.string.setting_account_email,
                    icon = 0,
                    menuType = SettingMenuType.SETTING_EMAIL
                ),
                MenuItem(
                    menuName = R.string.setting_account_password,
                    icon = 0,
                    menuType = SettingMenuType.SETTING_PASSWORD
                )
            )
        ),
        SettingMenuUiModel(
            header = R.string.setting_account_section_account_control,
            menuItem = listOf(
                MenuItem(
                    menuName = R.string.setting_account_delete_account,
                    icon = 0,
                    menuType = SettingMenuType.ABOUT_US
                )
            )
        )
    )

    val staticPage = PageHeaderUiModel(
        pageUiItem = listOf(
            PageUiModel(
                avatarUrl = "https://api.time.com/wp-content/uploads/2015/11/adam-driver.jpg"
            ),
            PageUiModel(
                avatarUrl = "https://dudeplace.co/wp-content/uploads/2018/11/stanlee.jpg"
            ),
            PageUiModel(
                avatarUrl = "https://thestandard.co/wp-content/uploads/2020/12/johnny-depp-wished-the-year-2021.jpg"
            )
        )
    )
}