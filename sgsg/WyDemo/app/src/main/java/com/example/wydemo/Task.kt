package com.example.wydemo

import android.graphics.Bitmap
import android.util.Log
import okhttp3.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URISyntaxException

interface createTaskCallBack {
    fun onSuccess(response: String)
}


class Task(
    val title: String,
    val contactNumber: String,
    val content: String,
    val pics: ArrayList<String>,
    val tags: ArrayList<String>,
    val time: String,
) {
    companion object {
        val types: HashMap<String, Int>
            get() {
                val types = HashMap<String, Int>()
                types["lecture"] = 0
                types["purchasing"] = 1
                types["reward"] = 2
                types["study"] = 3
                types["idle"] = 4
                types["lostProperty"] = 5
                types["jobs"] = 6
                return types
            }

        fun createTask(
            type: String,
            args: HashMap<String, String>,
            listener: createTaskCallBack,
            arrayArgs: HashMap<String, ArrayList<String>>? = null,
        ) {
            val relaAddress = "/project/" + type + "/create"
            HttpUtil.sendRequestWithOkHttp(relaAddress, args, object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    Log.d("sgsg", "发送请求成功")
                    val responseData: String? = response.body()?.string()
                    Log.d("sgsg", responseData.toString())
                    listener.onSuccess((responseData.toString()))
                }

                override fun onFailure(call: Call, e: IOException) {
                    Log.d("sgsg", "发送请求失败")
                    e.printStackTrace()
                }
            }, arrayArgs)
        }

        /**
         * 质量压缩
         * 设置bitmap options属性，降低图片的质量，像素不会减少
         * 第一个参数为需要压缩的bitmap图片对象，第二个参数为压缩后图片保存的位置
         * 设置options 属性0-100，来实现压缩（因为png是无损压缩，所以该属性对png是无效的）
         *
         * @param bmp
         * @param file
         */
        fun compressPicture(bmp: Bitmap, imgPath: String) {
            val file = File(imgPath)
            Log.d("sgsg", file.length().toString())
            val quality = 20
            val baos = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.JPEG, quality, baos)
            try {
                val fos = FileOutputStream(file)
                fos.write(baos.toByteArray())
                fos.flush()
                fos.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Log.d("sgsg", file.length().toString())
        }

        // 上传背景图片的方法
        fun uploadImg(userOpenId: String, imgPath: String, callback: okhttp3.Callback) {
            var file: File? = null
            try {
                file = File(imgPath)
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }

            val mOkHttpClent = OkHttpClient()
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("userOpenId", userOpenId)  // 上传参数
                .addFormDataPart(
                    "file", file?.name,
                    RequestBody.create(MediaType.parse("multipart/form-data"), file!!)
                )   // 上传文件
                .build()

            val request = Request.Builder()
                .url("http://www.involute.cn:8886/wy/util/uploadFile")
                .post(requestBody)
                .build()
            val call = mOkHttpClent.newCall(request)
            call.enqueue(callback)
        }

    }
}