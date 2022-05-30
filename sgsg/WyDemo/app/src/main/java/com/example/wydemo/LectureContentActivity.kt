package com.example.wydemo

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_lecture.*
import kotlinx.android.synthetic.main.activity_lecture_content.*


class LectureContentActivity : AppCompatActivity() {
    //图片放大显示
    private lateinit var dialog: Dialog
    private lateinit var image: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lecture_content)
        //图片放大初始化
        init()
        //接收数据
        val time = intent.getStringExtra("time")
        val titleData = intent.getStringExtra("title")
        lectureTitle.setText(titleData)
        //数据初始化
        LectureContent.init(time!!, object : LectureContentDataCallback {
            override fun onFinish(response: ArrayList<LectureContent>) {
                runOnUiThread {
                    val layoutManager = LinearLayoutManager(applicationContext)
                    lectureContentRecyclerView.layoutManager = layoutManager
                    val adapter = LectureContentAdapter(response, object : OnClickCallback {
                        override fun onClick(view: View, position: Int) {
                            //图片全屏显示
                            //设置图片资源
                            Glide.with(applicationContext)
                                .load("http://"+response[position].url)
                                .into(image)
                            //显示全屏dialog
                            dialog.show()

                        }
                    })
                    lectureContentRecyclerView.adapter = adapter
                }
            }

            override fun onError(e: Exception) {
                e.printStackTrace()
            }

        })
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

