package com.castcle.networking.service.interceptor

import java.io.*
import java.net.HttpURLConnection
import java.net.URL

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
//  Created by sklim on 8/12/2021 AD at 13:49.

object PublicIP {

    fun get(verbose: Boolean = false): String? {
        val stringUrl = "https://ipinfo.io/ip"
        try {
            val url = URL(stringUrl)
            val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            if (verbose) {
                val responseCode: Int = conn.responseCode
                println("\nSending 'GET' request to URL : $url")
                println("Response Code : $responseCode")
            }
            val response = StringBuffer()
            val `in` = BufferedReader(
                InputStreamReader(conn.inputStream)
            )
            var inputLine: String?
            while (`in`.readLine().also { inputLine = it } != null) {
                response.append(inputLine)
            }
            `in`.close()
            if (verbose) {
                //print result
                println("My Public IP address:$response")
            }
            return response.toString()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
}