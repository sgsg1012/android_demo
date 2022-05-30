package com.example.wydemo

import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException

interface LectureContentDataCallback {
    fun onFinish(response: ArrayList<LectureContent>)
    fun onError(e: Exception)
}

class LectureContent(val college: String, val url: String) {
    companion object {
        private var relaAddress = "/whu/lectureInfo"
        private val args = HashMap<String, String>()
        val data = ArrayList<LectureContent>()

        fun init(time: String, listener: LectureContentDataCallback) {
            data.clear()
            args.clear()
            args["time"] = time
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

        fun addData(responseData: String, listener: LectureContentDataCallback) {
            try {
                val jsonArray = JSONArray(responseData)
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val college = jsonObject.getString("college")
                    val url = jsonObject.getString("imageUrl")
                    data.add(LectureContent(college, url))
                }
                listener.onFinish(data)
            } catch (e: Exception) {
                e.printStackTrace()
                listener.onError(e)
            }
        }

    }
}