package com.castcle.components_android.ui.custom.previewlinkurl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

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
//  Created by sklim on 25/8/2021 AD at 17:23.

private const val ELEMENT_TAG_META = "meta"
private const val DOC_SELECT_QUERY = "meta[property^=og:]"
private const val ATTRIBUTE_VALUE_PROPERTY = "property"
private const val ATTRIBUTE_VALUE_NAME = "name"
private const val ATTRIBUTE_VALUE_ITEMPROP = "itemprop"
private const val AGENT = "Mozilla"

/* for <meta property="og:" to get title */
private val META_OG_TITLE = arrayOf("og:title", "\"og:title\"", "'og:title'")

/* for <meta property="og:" to get description */
private val META_OG_DESCRIPTION =
    arrayOf("og:description", "\"og:description\"", "'og:description'")

/* for <meta property="og:" to get image */
private val META_OG_IMAGE = arrayOf("og:image", "\"og:image\"", "'og:image'")

private val META_OG_IMAGE_ICON = arrayOf("href", "\"href\"", "'href'")

/*for <meta name=... to get title */
private val META_NAME_TITLE = arrayOf(
    "twitter:title",
    "\"twitter:title\"",
    "'twitter:title'",
    "title",
    "\"title\"",
    "'title'"
)

private val META_NAME_DESCRIPTION = arrayOf(
    "twitter:description",
    "\"twitter:description\"",
    "'twitter:description'",
    "description",
    "\"description\"",
    "'description'"
)

/*for <meta name=... to get image */
private val META_NAME_IMAGE = arrayOf(
    "twitter:image",
    "\"twitter:image\"",
    "'twitter:image'"
)

/*for <meta itemprop=... to get title */
private val META_ITEMPROP_TITLE = arrayOf("name", "\"name\"", "'name'")

/*for <meta itemprop=... to get description */
private val META_ITEMPROP_DESCRIPTION = arrayOf("description", "\"description\"", "'description'")

/*for <meta itemprop=... to get image */
private val META_ITEMPROP_IMAGE = arrayOf("image", "\"image\"", "'image'")

private const val CONTENT = "content"
private const val CONTENT_HREF = "href"
private const val CONTENT_ICON = "mask-icon"
private const val CONTENT_SHORT_ICON = "shortcut icon"
private const val CONTENT_REL = "rel"
private const val CONTENT_COLOR = "color"
private const val LINK_TYPE_TWITTER = "twitter"

suspend fun getDocument(url: String, timeOut: Int = 300): Document =
    withContext(Dispatchers.IO) {
        return@withContext Jsoup.connect(url)
            .userAgent(AGENT)
            .ignoreContentType(true)
            .followRedirects(true)
            .timeout(timeOut)
            .get()
    }

suspend fun parseHtml(document: Document, linkType: String): UrlInfoUiModel =
    withContext(Dispatchers.IO) {
        val metaTags = document.getElementsByTag(ELEMENT_TAG_META)
        val urlInfo = UrlInfoUiModel()

        if (linkType.equals(LINK_TYPE_TWITTER, false)) {
            val metaTwitter = metaTags.find {
                it.hasAttr(ATTRIBUTE_VALUE_PROPERTY)
            }

            metaTwitter?.let { it ->
                it.parentNode()?.childNodes()?.find {
                    it.attr(CONTENT_REL) == CONTENT_ICON
                }?.let {
                    urlInfo.imageIcon = it.attr(CONTENT_HREF)
                    urlInfo.bgColotIcon = it.attr(CONTENT_COLOR)
                }
            }
        } else {
            metaTags.forEach { it ->
                when (it.attr(ATTRIBUTE_VALUE_PROPERTY)) {
                    in META_NAME_TITLE -> if (urlInfo.title.isEmpty()) urlInfo.title =
                        it.attr(CONTENT)
                    in META_OG_TITLE -> if (urlInfo.title.isEmpty()) urlInfo.title =
                        it.attr(CONTENT)
                    in META_OG_DESCRIPTION -> if (urlInfo.description.isEmpty()) urlInfo.description =
                        it.attr(CONTENT)
                    in META_NAME_IMAGE -> if (urlInfo.image.isEmpty()) urlInfo.image =
                        it.attr(CONTENT)
                    in META_OG_IMAGE_ICON -> if (urlInfo.image.isEmpty()) urlInfo.image =
                        it.attr(CONTENT_HREF)
                }

                when (it.attr(ATTRIBUTE_VALUE_NAME)) {
                    in META_NAME_TITLE -> if (urlInfo.title.isEmpty()) urlInfo.title =
                        it.attr(CONTENT)
                    in META_NAME_DESCRIPTION -> if (urlInfo.description.isEmpty()) urlInfo.description =
                        it.attr(CONTENT)
                    in META_OG_IMAGE -> if (urlInfo.image.isEmpty()) urlInfo.image =
                        it.attr(CONTENT)
                    in META_NAME_IMAGE -> if (urlInfo.image.isEmpty()) urlInfo.image =
                        it.attr(CONTENT)
                }

                when (it.attr(ATTRIBUTE_VALUE_ITEMPROP)) {
                    in META_ITEMPROP_TITLE -> if (urlInfo.title.isEmpty()) urlInfo.title =
                        it.attr(CONTENT)
                    in META_ITEMPROP_DESCRIPTION -> if (urlInfo.description.isEmpty()) urlInfo.description =
                        it.attr(
                            CONTENT
                        )
                    in META_ITEMPROP_IMAGE -> if (urlInfo.image.isEmpty()) urlInfo.image = it.attr(
                        CONTENT
                    )
                }
                if (urlInfo.allFetchComplete()) {
                    return@withContext urlInfo
                }

            }
        }
        return@withContext urlInfo
    }
