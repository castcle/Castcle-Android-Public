package com.castcle.data.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import com.castcle.common_model.model.feed.ContentDbModel
import com.castcle.common_model.model.feed.PageKey
import com.castcle.common_model.model.feed.domain.dao.FeedCacheDao
import com.castcle.common_model.model.feed.domain.dao.FeedCacheModel
import com.castcle.common_model.model.userprofile.User
import com.castcle.common_model.model.userprofile.UserPage
import com.castcle.data.model.dao.feed.CommentDao
import com.castcle.common_model.model.feed.domain.dao.PageKeyDao
import com.castcle.common_model.model.feed.domain.dao.UserDao
import com.castcle.data.model.dao.user.UserPageDao

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
//  Created by sklim on 31/8/2021 AD at 16:39.
@Database(
    entities = [
        User::class,
        ContentDbModel::class,
        PageKey::class,
        UserPage::class,
        FeedCacheModel::class
    ], version = 8
)
abstract class CastcleDataBase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun userPageDao(): UserPageDao
    abstract fun commentDao(): CommentDao
    abstract fun pageKeyDao(): PageKeyDao
    abstract fun feedCacheDao(): FeedCacheDao
}

const val DATABASE_NAME = "Castcle Database"