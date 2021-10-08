package com.castcle.common_model.model.feed.converter

import androidx.room.TypeConverter
import com.castcle.common_model.model.feed.PayLoadUiModel
import com.castcle.common_model.model.feed.PayloadContentUiModel
import com.castcle.common_model.model.feed.api.response.PayloadContent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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
//  Created by sklim on 6/10/2021 AD at 09:40.

class PayloadContentConverter {

    private val jsonType = object : TypeToken<PayloadContentUiModel>() {}.type

    @TypeConverter
    fun toChildren(children: String): PayloadContentUiModel {
        return Gson().fromJson(children, jsonType)
    }

    @TypeConverter
    fun fromChildren(children: PayloadContentUiModel): String {
        return Gson().toJson(children, jsonType)
    }
}