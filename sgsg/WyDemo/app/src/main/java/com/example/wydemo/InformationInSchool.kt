package com.example.wydemo

import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException

interface InformationInSchoolDataCallBack {
    fun onFinish(response: ArrayList<InformationInSchool>)
    fun onError(e: Exception)
}


class InformationInSchool(val time: String, val url: String, val title: String) {

    companion object {
        var bottom: Boolean = false
        private var relaAddress = "/whu/notice"
        private val args = HashMap<String, String>()
        var page = 1
        val data = ArrayList<InformationInSchool>()

        //接收一页数据用于初始化data
        fun init(listener: InformationInSchoolDataCallBack) {
            data.clear()
            args.clear()
            bottom = false
            page = 1
            args["page"] = "1"
            HttpUtil.sendRequestWithOkHttp(relaAddress, args, object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val responseData: String? = response.body()?.string()
                    if (responseData != null) {
                        addData(responseData, listener)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    Log.d("sgsg", "发送请求失败")
                    e.printStackTrace()
                    listener.onError(e)
                }
            })
        }

        fun append(listener: InformationInSchoolDataCallBack) {
            args.clear()
            page = page + 1
            args["page"] = page.toString()
            HttpUtil.sendRequestWithOkHttp(relaAddress, args, object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    //Log.d("sgsg", "发送请求成功")
                    val responseData: String? = response.body()?.string()
                    if (responseData != null) {
                        addData(responseData, listener)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    listener.onError(e)
                }
            })
        }

        fun addData(responseData: String) {
            if (responseData == "[null]") {
                bottom = true
                return
            }
            try {
                val jsonArray = JSONArray(responseData)
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val time = jsonObject.getString("date")
                    val url = jsonObject.getString("href")
                    val title = jsonObject.getString("title")
                    data.add(InformationInSchool(time, url, title))
                    //Log.d("sgsg", data.size.toString())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun addData(responseData: String, listener: InformationInSchoolDataCallBack) {
            if (responseData == "[null]") {
                bottom = true
                listener.onFinish(data)
                return
            }
            // Log.d("sgsg", responseData)
            try {
                val jsonArray = JSONArray(responseData)
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val time = jsonObject.getString("date")
                    val url = jsonObject.getString("href")
                    val title = jsonObject.getString("title")
                    data.add(InformationInSchool(time, url, title))
                    //Log.d("sgsg", data.size.toString())
                }
                listener.onFinish(data)
            } catch (e: Exception) {
                e.printStackTrace()
                listener.onError(e)
            }
        }
    }
}