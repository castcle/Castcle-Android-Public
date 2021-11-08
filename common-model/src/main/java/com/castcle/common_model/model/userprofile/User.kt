package com.castcle.common_model.model.userprofile

import androidx.room.*
import com.castcle.common_model.model.setting.CreatePageResponse
import com.google.gson.Gson

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
//  Created by sklim on 31/8/2021 AD at 17:04.
@Entity(tableName = "user")
data class User(
    @PrimaryKey
    @ColumnInfo(name = "castcle_id") val castcleId: String,
    @ColumnInfo(name = "displayName") val displayName: String,
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "dob") val dob: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "avatar") val avatar: String,
    @ColumnInfo(name = "avatarThumbnail") val avatarThumbnail: String,
    @ColumnInfo(name = "cover") val cover: String,
    @ColumnInfo(name = "followed") val followed: Boolean,
    @ColumnInfo(name = "followers_count") val followersCount: Int,
    @ColumnInfo(name = "following_count") val followingCount: Int,
    @ColumnInfo(name = "overview") val overview: String,
    @ColumnInfo(name = "verified") val verified: Boolean = false,
    @ColumnInfo(name = "mobile") val mobile: Boolean = false,
    @ColumnInfo(name = "official") val official: Boolean = false,
    @ColumnInfo(name = "facebook_links") val facebookLinks: String,
    @ColumnInfo(name = "medium_links") val mediumLinks: String,
    @ColumnInfo(name = "twitter_links") val twitterLinks: String,
    @ColumnInfo(name = "website_links") val websiteLinks: String,
    @ColumnInfo(name = "youtube_links") val youtubeLinks: String
)

fun UserProfileResponse.toUserProfile(): User {
    return User(
        castcleId = castcleId,
        displayName = displayName ?: "",
        id = id ?: "",
        dob = dob ?: "",
        email = email ?: "",
        avatar = images.avatar.original ?: "",
        avatarThumbnail = images.avatar.thumbnail ?: "",
        cover = images.cover.original ?: "",
        followed = followed,
        followersCount = followers.count,
        followingCount = following.count,
        overview = overview ?: "",
        facebookLinks = links?.facebook ?: "",
        mediumLinks = links?.medium ?: "",
        twitterLinks = links?.twitter ?: "",
        websiteLinks = links?.website ?: "",
        youtubeLinks = links?.youtube ?: "",
        verified = verified?.email ?: false
    )
}

fun User.toStringModel(): String {
    return Gson().toJson(this)
}

fun String.toUserModel(): User {
    return Gson().fromJson(this, User::class.java)
}

fun CreatePageResponse.toUserPage(): User {
    return User(
        castcleId = castcleId,
        displayName = displayName ?: "",
        id = id ?: "",
        dob = "",
        email = "",
        avatar = images.avatar?.original ?: "",
        avatarThumbnail = images.avatar?.thumbnail ?: "",
        cover = images.cover?.original ?: "",
        followed = false,
        followersCount = 0,
        followingCount = 0,
        overview = overview ?: "",
        facebookLinks = links?.facebook ?: "",
        mediumLinks = links?.medium ?: "",
        twitterLinks = links?.twitter ?: "",
        websiteLinks = links?.website ?: "",
        youtubeLinks = links?.youtube ?: "",
        verified = false
    )
}