package com.example.wydemo

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.youth.banner.Banner
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.CircleIndicator
import com.youth.banner.listener.OnBannerListener
import kotlinx.android.synthetic.main.activity_reward_task_content.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.util.regex.Matcher
import java.util.regex.Pattern


class RewardTaskContentActivity : AppCompatActivity() {

    //图片放大显示
    private lateinit var dialog: Dialog
    private lateinit var image: ImageView

    var projectId = ""
    var title = ""
    var time = ""
    var content = ""
    var contact = ""
    lateinit var tagList: ArrayList<String>
    val pictures = ArrayList<String>()
    val args = HashMap<String, String>()
    val relaAddress = "/project/reward/findOne"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reward_task_content)

        //接收数据
        projectId = intent.getStringExtra("projectId").toString()

        //图片全屏显示
        init()

        initData(object : callback {
            override fun onFinish() {
                runOnUiThread {
                    taskTitle.setText(title)
                    taskTime.setText(time)
                    taskContent.setText(content)
                    taskContact.setText("QQ: " + contact)
                    val len = tagList.size
                    if (len > 0) {
                        tagArea.visibility = View.VISIBLE
                        tag1.visibility = View.VISIBLE
                        tag1Text.setText(tagList[0])
                        if (len > 1) {
                            tag2.visibility = View.VISIBLE
                            tag2Text.setText(tagList[1])
                            if (len > 2) {
                                tag3.visibility = View.VISIBLE
                                tag3Text.setText(tagList[2])
                            }
                        }
                    }
                    if (pictures.size == 0) pictures.add("www.involute.cn:/image/15090585371_982925_1924df912ca8-ceca-4b53-8bb7-be2893b7221f.gif")
                    //轮播图
                    val rotationMap = rotationMap as Banner<String, BannerImageAdapter<String>>
                    rotationMap.addBannerLifecycleObserver(this@RewardTaskContentActivity).setIndicator(CircleIndicator(applicationContext))
                    rotationMap.setAdapter(object : BannerImageAdapter<String>(pictures) {
                        override fun onBindView(
                            holder: BannerImageHolder,
                            data: String,
                            position: Int,
                            size: Int,
                        ) {
                            //图片加载自己实现
                            val url = "http://" + data
                                .replace("\\/", "/").replace(":/", "/")
                            Glide.with(holder.itemView)
                                .load(url)
                                .into(holder.imageView)
                        }
                    })
                    rotationMap.setOnBannerListener(object : OnBannerListener<String> {
                        override fun OnBannerClick(data: String?, position: Int) {
                            //图片全屏显示
                            //设置图片资源
                            val url = "http://" + data
                                ?.replace("\\/", "/")?.replace(":/", "/")
                            Glide.with(applicationContext)
                                .load(url)
                                .into(image)
                            //显示全屏dialog
                            dialog.show()
                        }

                    })
                }
            }

        })


    }

    private fun initData(callback: callback) {
        args.clear()
        args["projectId"] = projectId
        HttpUtil.sendRequestWithOkHttp(relaAddress, args, object : Callback {
            override fun onResponse(call: Call, response: Response) {
                //Log.d("sgsg", "发送请求成功")
                val responseData: String? = response.body()?.string()
                if (responseData != null) {
                    //Log.d("sgsg", responseData)
                    parseData(responseData)
                    callback.onFinish()
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
        })
    }

    private fun parseData(data: String) {
        try {
            val jsonObject = JSONObject(data)
            val responseData2 = jsonObject.getString("data")
            val jsonData = JSONObject(responseData2)
            title = jsonData.getString("title")
            content = jsonData.getString("content")
            time = jsonData.getString("location").take(16)
            //picture
            val picture = jsonData.getString("picture")
            val p: Pattern = Pattern.compile("\"(.*?)\"")
            val m: Matcher = p.matcher(picture)
            while (m.find()) {
                val s: String = m.group().trim('"')
                if (s != "null") pictures.add(s)
            }
            contact = jsonData.getString("contactNumber")
            val tags = jsonData.getString("tags")
            if (tags != "null") {
                var flag = false
                for (c in tags) {
                    if (c == ',') {
                        flag = true
                        break
                    }
                }
                if (flag) tagList = tags.split(',') as ArrayList<String>
                else {
                    tagList = ArrayList<String>()
                    tagList.add(tags)
                }
            } else {
                tagList = ArrayList<String>()
            }
            //Log.d("sgsg", tagList.size.toString())
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("sgsg", "wrong!!!!!!")
        }
    }

    private fun init() {
        //展示在dialog上面的大图
        dialog = Dialog(this, R.style.FullActivity)
        image = ImageView(this)
        //示例图
        image.setImageResource(R.drawable.lecture_image)
        dialog.setContentView(image)
        //点击消失
        image.setOnClickListener { dialog.dismiss() }
    }
}