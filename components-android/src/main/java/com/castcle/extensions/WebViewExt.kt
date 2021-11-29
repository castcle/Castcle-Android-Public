package com.castcle.extensions

import android.annotation.SuppressLint
import android.graphics.Color
import android.webkit.WebSettings
import android.webkit.WebView
import com.castcle.components_android.ui.font.Font

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
//  Created by sklim on 19/11/2021 AD at 10:53.

@SuppressLint("SetJavaScriptEnabled")
fun WebView.setupForWebApp() {
    settings.javaScriptEnabled = true
    settings.domStorageEnabled = true
    settings.allowContentAccess = true
    settings.allowFileAccess = true
    settings.setSupportZoom(false)
}

fun WebView.loadHtml(
    content: String?,
    fontColor: Int,
    font: Font = Font.KANIT,
    fontSizePx: String = "14px"
) {
    val textColorString = Integer.toHexString(fontColor).substring(2)
    val text = "<!DOCTYPE html>\n" +
        "<html>\n" +
        "  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" +
        "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
        "<style type='text/css'>\n" +
        "  @font-face {\n" +
        "    font-family: ${font.fontFamily};\n" +
        "    src: url('${font.regular}');\n" +
        "    font-style: normal;\n" +
        "    font-weight: normal;\n" +
        "    font-variant-ligatures: no-common-ligatures;\n" +
        "  }\n" +
        "  @font-face {\n" +
        "    font-family: ${font.fontFamily};\n" +
        "    src: url('${font.bold}');\n" +
        "    font-style: bold;\n" +
        "    font-weight: bold;\n" +
        "    font-variant-ligatures: no-common-ligatures;\n" +
        "  }\n" +
        "  body p {\n" +
        "    font-family: ${font.fontFamily};\n" +
        "    font-weight: normal;\n" +
        "    font-variant-ligatures: no-common-ligatures;\n" +
        "  }\n" +
        "p, span, strong, h1, div , ul, li, b, a:link, a:visited {\n" +
        "  font-family: ${font.fontFamily};\n" +
        "  color: #$textColorString;\n" +
        "  font-size: $fontSizePx;\n" +
        "  font-variant-ligatures: no-common-ligatures;\n" +
        "}\n" +
        "  img{\n" +
        "  display: inline;\n" +
        "  height: auto;\n" +
        "  max-width: 100%;\n" +
        "  }\n" +
        "</style>\n" +
        "<body>\n" +
        "<p align='justify'>${content ?: ""}</p>\n" +
        "</body>\n" +
        "</html>"

    this.setupForWebApp()
    this.loadDataWithBaseURL(
        "file:///android_asset/fonts/",
        text,
        "text/html",
        "utf-8",
        null
    )
    this.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
    this.setBackgroundColor(Color.TRANSPARENT)
}