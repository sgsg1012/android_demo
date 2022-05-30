package com.example.wydemo

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_infos_in_school.*

class InfosInSchoolActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_infos_in_school)

        //组件初始化
        progressBar.visibility = View.GONE
        fab.visibility = View.GONE

        //页面数据初始化
        InformationInSchool.init(object : InformationInSchoolDataCallBack {
            override fun onFinish(data: ArrayList<InformationInSchool>) {
                runOnUiThread {
                    val layoutManager = LinearLayoutManager(applicationContext)
                    infosRecyclerView.layoutManager = layoutManager
                    val adapter = InfosAdapter(data)
                    infosRecyclerView.adapter = adapter
                }
            }

            override fun onError(e: Exception) {
                e.printStackTrace()
            }

        })
        InformationInSchool.append(object : InformationInSchoolDataCallBack {
            override fun onFinish(response: ArrayList<InformationInSchool>) {
                if (!InformationInSchool.bottom) {
                    runOnUiThread {
                        infosRecyclerView.adapter?.notifyDataSetChanged()
                    }
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

        fab.setOnClickListener {
            infosRecyclerView.smoothScrollToPosition(0)
        }
        //滑动监听
        infosRecyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
                if (lastItemPosition >= recyclerView.adapter?.itemCount?.minus(1)!! && !InformationInSchool.bottom && dy > 0) {
                    runOnUiThread { progressBar.visibility = View.VISIBLE }
                    InformationInSchool.append(object : InformationInSchoolDataCallBack {
                        override fun onFinish(data: ArrayList<InformationInSchool>) {
                            runOnUiThread { progressBar.visibility = View.GONE }
                            if (InformationInSchool.bottom) {
                                runOnUiThread {
                                    Toast.makeText(applicationContext,
                                        "到底了",
                                        Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                runOnUiThread {
                                    recyclerView.adapter?.notifyDataSetChanged()
                                }
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
        InformationInSchool.init(object : InformationInSchoolDataCallBack {
            override fun onFinish(data: ArrayList<InformationInSchool>) {
                runOnUiThread { infosRecyclerView.adapter?.notifyDataSetChanged() }
                InformationInSchool.append(object : InformationInSchoolDataCallBack {
                    override fun onFinish(response: ArrayList<InformationInSchool>) {
                        runOnUiThread { refresh.isRefreshing = false }
                        if (!InformationInSchool.bottom) {
                            runOnUiThread {
                                infosRecyclerView.adapter?.notifyDataSetChanged()
                            }
                        }
                    }

                    override fun onError(e: Exception) {
                        e.printStackTrace()
                    }

                })
            }

            override fun onError(e: Exception) {
                e.printStackTrace()
            }
        })
    }
}