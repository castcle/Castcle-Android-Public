package com.castcle.di.component

import android.content.Context
import com.castcle.authen_android.di.components.dependencies.AuthenticateStorageComponent
import com.castcle.common_model.model.feed.domain.dao.*
import com.castcle.data.model.dao.feed.CommentDao
import com.castcle.common_model.model.feed.domain.dao.UserDao
import com.castcle.data.model.dao.user.UserPageDao
import com.castcle.data.storage.*
import com.castcle.di.storage.StorageModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

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
//  Created by sklim on 18/8/2021 AD at 18:59.
@Singleton
@Component(modules = [StorageModule::class])
interface StorageComponent : AuthenticateStorageComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): StorageComponent
    }

    fun userDao(): UserDao

    fun userPageDao(): UserPageDao

    fun commentDao(): CommentDao

    fun pageKeyDao(): PageKeyDao

    fun appPreferences(): AppPreferences

    fun deviceSettings(): DeviceSettings

    fun feedCacheDao(): FeedCacheDao

    fun castcleDataBase(): CastcleDataBase
}
