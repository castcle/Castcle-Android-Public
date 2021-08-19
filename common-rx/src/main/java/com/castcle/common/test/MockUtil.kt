package com.castcle.common.test

import com.google.gson.GsonBuilder
import java.io.BufferedReader
import java.io.InputStreamReader

object MockTool {
    inline fun <reified T> getResponse(fileName: String): T {
        val gson = GsonBuilder().create()
        return gson.fromJson(getReader("response/$fileName"), T::class.java)
    }

    fun getReader(filePath: String): BufferedReader {
        val resource = this.javaClass.classLoader!!.getResource(filePath)
        return BufferedReader(InputStreamReader(resource.openStream()))
    }
}
