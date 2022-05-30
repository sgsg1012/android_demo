package com.example.wydemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_lecture.*
import kotlinx.android.synthetic.main.activity_lecture.fab
import kotlinx.android.synthetic.main.activity_lecture.progressBar
import kotlinx.android.synthetic.main.activity_lecture.refresh

class LectureActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lecture)

        //组件初始化
        progressBar.visibility = View.GONE
        fab.visibility = View.GONE

        //页面数据初始化
        Lecture.init(object : LectureDataCallBack {
            override fun onFinish(data: ArrayList<Lecture>) {
                runOnUiThread {
                    val layoutManager = LinearLayoutManager(applicationContext)
                    lectureRecyclerView.layoutManager = layoutManager
                    val adapter = LectureAdapter(data)
                    lectureRecyclerView.adapter = adapter
                }
            }

            override fun onError(e: Exception) {
                e.printStackTrace()
            }

        })

        //下拉刷新
        refresh.setColorSchemeResources(R.color.teal_200)
        refresh.setOnRefreshListener {
            refresh()
        }
        //回到顶部
        fab.setOnClickListener {
            lectureRecyclerView.smoothScrollToPosition(0)
        }
        lectureRecyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager: RecyclerView.LayoutManager? = recyclerView.layoutManager
                val linearManager = layoutManager as LinearLayoutManager
                //获取最后一个可见view的位置
                val lastItemPosition = linearManager.findLastVisibleItemPosition()
                val firstItemPosition = linearManager.findFirstVisibleItemPosition()
                if (firstItemPosition > 0) {
                    runOnUiThread { fab.visibility = View.VISIBLE }
                } else {
                    runOnUiThread { fab.visibility = View.GONE }
                }
                if (lastItemPosition >= recyclerView.adapter?.itemCount?.minus(1)!! && !Lecture.bottom && dy > 0) {
                    runOnUiThread { progressBar.visibility = View.VISIBLE }
                    Lecture.append(object : LectureDataCallBack {
                        override fun onFinish(data: ArrayList<Lecture>) {
                            runOnUiThread { progressBar.visibility = View.GONE }
                            if (Lecture.bottom) {
                                runOnUiThread {
                                    Toast.makeText(applicationContext,
                                        "到底了",
                                        Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                runOnUiThread { recyclerView.adapter?.notifyDataSetChanged() }
                            }
                        }

                        override fun onError(e: Exception) {
                            e.printStackTrace()
                        }
                    })
                }

            }
        })
    }

    private fun refresh() {
        Lecture.init(object : LectureDataCallBack {
            override fun onFinish(data: ArrayList<Lecture>) {
                runOnUiThread {
                    refresh.isRefreshing = false
                    lectureRecyclerView.adapter?.notifyDataSetChanged()
                }
            }

            override fun onError(e: Exception) {
                e.printStackTrace()
            }

        })
    }
}