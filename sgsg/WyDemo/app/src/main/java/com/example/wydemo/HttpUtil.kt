package com.example.wydemo

import okhttp3.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

object HttpUtil {
    private val rootAddress = "http://www.involute.cn:8886/wy"

    fun sendRequestWithOkHttp(
        relaAddress: String,
        args: HashMap<String, String>,
        callback: Callback,
        arrayArgs: HashMap<String, ArrayList<String>>? = null,
    ) {
        val client = OkHttpClient()
        var requestBodyBuilder = FormBody.Builder()
        for ((key, value) in args) {
            requestBodyBuilder.add(key, value)
        }
        if (arrayArgs != null) {
            for ((key, valueArray) in arrayArgs) {
                for (value in valueArray) {
                    requestBodyBuilder.add(key, value)
                }
            }
        }
        val requestBody = requestBodyBuilder.build()
        val request = Request.Builder().url(rootAddress + relaAddress).post(requestBody).build()
        client.newCall(request).enqueue(callback)
    }
}

//行不通
object HttpUtilOnMainThread {
    private val rootAddress = "http://www.involute.cn:8886/wy"

    fun sendRequestWithOkHttp(
        relaAddress: String,
        args: HashMap<String, String>,
        callback: okhttp3.Callback,
    ) {
        try {
            val client = OkHttpClient()
            val request = if (args.size > 0) {
                var requestBodyBuilder = FormBody.Builder()
                for ((key, value) in args) {
                    requestBodyBuilder.add(key, value)
                }
                val requestBody = requestBodyBuilder.build()

                Request.Builder().url(HttpUtilOnMainThread.rootAddress + relaAddress)
                    .post(requestBody)
                    .build()
            } else {
                Request.Builder().url(HttpUtilOnMainThread.rootAddress + relaAddress).build()
            }
            val response = client.newCall(request).execute()
            val responseData = response.body()?.string()
            if (responseData != null) {
//                showResponse(responseData)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}

//HttpUtil.sendRequestWithOkHttp(address, object : Callback {
//    override fun onResponse(call: Call, response: Response) {
//        val responseData = response.body?.string()
//        // TODO: 2022/1/15
//    }
//
//    override fun onFailure(call: Call, e: IOException) {
//        TODO("Not yet implemented")
//    }
//})