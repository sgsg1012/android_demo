package com.example.wydemo

import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

interface LectureDataCallBack {
    fun onFinish(response: ArrayList<Lecture>)
    fun onError(e: Exception)
}


class Lecture(val time: String, val title: String) {
    companion object {
        var bottom: Boolean = false
        private var relaAddress = "/whu/lectureList"
        private val args = HashMap<String, String>()
        var page = 1
        private const val pageSize = 40
        val data = ArrayList<Lecture>()

        //接收一页数据用于初始化data
        fun init(listener: LectureDataCallBack) {
            data.clear()
            args.clear()
            bottom = false
            page = 1
            args["page"] = "1"
            args["size"] = pageSize.toString()
            HttpUtil.sendRequestWithOkHttp(relaAddress, args, object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    //Log.d("sgsg", "发送请求成功")
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

        fun append(listener: LectureDataCallBack) {
            args.clear()
            page = page + 1
            args["page"] = page.toString()
            args["size"] = pageSize.toString()
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

        fun addData(responseData: String, listener: LectureDataCallBack) {
            try {
                val jsonObject = JSONObject(responseData)
                val responseData2 = jsonObject.getString("data")
                if (responseData2 == "[]") {
                    bottom = true
                    listener.onFinish(data)
                    return
                }
                val jsonArray = JSONArray(responseData2)
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val time = jsonObject.getString("time")
                    val title = jsonObject.getString("title")
                    data.add(Lecture(time, title))
                }
                listener.onFinish(data)
            } catch (e: Exception) {
                e.printStackTrace()
                listener.onError(e)
            }
        }
    }

}