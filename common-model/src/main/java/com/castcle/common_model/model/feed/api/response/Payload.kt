package com.castcle.common_model.model.feed.api.response

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
//  Created by sklim on 22/11/2021 AD at 12:23.
data class PayLoadList(
    @SerializedName("Payload")
    val payLoadLists: List<Payload>,
    @SerializedName("includes")
    val includes: IncludesResponse? = null,
    @SerializedName("meta")
    var meta: Meta,
)

data class Payload(
    @SerializedName("id") var id: String,
    @SerializedName("feature") var feature: Feature,
    @SerializedName("circle") var circle: Circle? = null,
    @SerializedName("type") var type: String,
    @SerializedName("payload") var payload: PayloadObjectContent,
    @SerializedName("aggregator") val aggregator: AggregatorContent? = null,
    @SerializedName("createdAt") var created: String,
    @SerializedName("updatedAt") var updated: String,
)

data class Meta(
    val newestId: String,// ใส่ param untilId เพื่อขอ content ถัดไป (load more)
    val oldestId: String,// ใส่ param sinceId เพื่อขอ content ล่าสุด
    val resultCount: Int //5..100 default 25
)
