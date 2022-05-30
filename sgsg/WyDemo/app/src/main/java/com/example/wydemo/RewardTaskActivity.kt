package com.example.wydemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_lecture.*
import kotlinx.android.synthetic.main.activity_reward_task.*
import kotlinx.android.synthetic.main.activity_reward_task.fab
import kotlinx.android.synthetic.main.activity_reward_task.progressBar
import kotlinx.android.synthetic.main.activity_reward_task.refresh
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

interface callback {
    fun onFinish()
}

class RewardTaskActivity : AppCompatActivity() {
    private val rewardTaskList = ArrayList<RewardTaskClass>()
    private val args = HashMap<String, String>()
    private var bottom: Boolean = false
    private var page = 1
    private var relaAddress = "/project/reward/list"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reward_task)

        //组件初始化
        progressBar.visibility = View.GONE
        fab.visibility = View.GONE

        //recyclerView
        val layoutManager = LinearLayoutManager(applicationContext)
        rewardTaskRecyclerView.layoutManager = layoutManager
        val adapter = RewardTaskClassAdapter(rewardTaskList)
        rewardTaskRecyclerView.adapter = adapter

        //初始化任务列表
        initData()

        //下拉刷新
        refresh.setColorSchemeResources(R.color.teal_200)
        refresh.setOnRefreshListener {
            refresh()
        }

        //回到顶部
        fab.setOnClickListener {
            rewardTaskRecyclerView.smoothScrollToPosition(0)
        }

        //recyclerView 点击事件
        rewardTaskRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
                if (lastItemPosition >= recyclerView.adapter?.itemCount?.minus(1)!! && !bottom && dy > 0) {
                    runOnUiThread { progressBar.visibility = View.VISIBLE }
                    //append
                    appendData(object :callback{
                        override fun onFinish() {
                            runOnUiThread { progressBar.visibility = View.GONE }
                            if(bottom){
                                runOnUiThread {
                                    Toast.makeText(applicationContext,
                                        "到底了",
                                        Toast.LENGTH_SHORT).show()
                                }
                            }else{
                                runOnUiThread {
                                    rewardTaskRecyclerView.adapter?.notifyDataSetChanged()
                                }
                            }
                        }
                    })
                }
            }
        })

    }

    private fun initData(
        callback: callback = object : callback {
            override fun onFinish() {

            }
        },
    ) {
        rewardTaskList.clear()
        args.clear()
        bottom = false
        page = 1
        args["page"] = page.toString()
        args["size"] = "20"
        HttpUtil.sendRequestWithOkHttp(relaAddress, args, object : Callback {
            override fun onResponse(call: Call, response: Response) {
                //Log.d("sgsg", "发送请求成功")
                val responseData: String? = response.body()?.string()
                if (responseData != null) {
//                    Log.d("sgsg",responseData)
                    parseData(responseData)
                    runOnUiThread {
                        rewardTaskRecyclerView.adapter?.notifyDataSetChanged()
                    }
                    callback.onFinish()
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
        })
    }

    private fun appendData(
        callback: callback = object : callback {
            override fun onFinish() {

            }
        },
    ) {
        args.clear()
        page = page + 1
        args["page"] = page.toString()
        args["size"] = "20"
        HttpUtil.sendRequestWithOkHttp(relaAddress, args, object : Callback {
            override fun onResponse(call: Call, response: Response) {
                //Log.d("sgsg", "发送请求成功")
                val responseData: String? = response.body()?.string()
                if (responseData != null) {
                    //Log.d("sgsg",responseData)
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
            if (responseData2 == "[]") {
                bottom = true
                return
            }
            val jsonArray = JSONArray(responseData2)
            for (i in 0 until jsonArray.length()) {
                //Log.d("sgsg", i.toString())
                val jsonObject = jsonArray.getJSONObject(i)
                //Log.d("sgsg", jsonObject.toString())
                val projectId = jsonObject.getString("projectId")
                val title = jsonObject.getString("title")
                var time = jsonObject.getString("location")
                time = time.take(16)
                val tags = jsonObject.getString("tags")
                lateinit var tagList: ArrayList<String>
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
                rewardTaskList.add(RewardTaskClass(projectId, title, time, tagList))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("sgsg", "wrong!!!!!!")
        }
    }

    private fun refresh() {
        initData(object : callback {
            override fun onFinish() {
                runOnUiThread {
                    refresh.isRefreshing = false
                }
            }

        })

    }


}