package com.example.wydemo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.youth.banner.Banner
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.CircleIndicator
import com.youth.banner.listener.OnBannerListener
import kotlinx.android.synthetic.main.home_fragment.view.*

import kotlinx.android.synthetic.main.activity_test.view.*
import kotlinx.android.synthetic.main.home_fragment.*


class HomeFragment : Fragment() {
    private lateinit var refresh: androidx.swiperefreshlayout.widget.SwipeRefreshLayout

    private lateinit var toolbar: Toolbar
    private lateinit var search: RelativeLayout
    private lateinit var msg: ImageButton
    private lateinit var schools: Spinner

    private lateinit var rotationMap: Banner<DataBean, BannerImageAdapter<DataBean>>

    private lateinit var func1: LinearLayout
    private lateinit var func2: LinearLayout
    private lateinit var func3: LinearLayout
    private lateinit var func4: LinearLayout
    private lateinit var func5: LinearLayout
    private lateinit var func6: LinearLayout
    private lateinit var func7: LinearLayout
    private lateinit var func8: LinearLayout

    private lateinit var realTimeInfo: TextView
    private lateinit var realTimeInfoContentText: TextView

    private lateinit var lecture: TextView
    private lateinit var lectureContent: com.google.android.material.card.MaterialCardView
    private lateinit var lectureContentTitle: TextView
    private lateinit var lectureContentTime: TextView

    private val schoolList = ArrayList<String>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val view = inflater.inflate(R.layout.home_fragment, container, false)
        initView(view)
        initSchools()

        initData()

        //下拉刷新
        refresh.setColorSchemeResources(R.color.teal_200)
        refresh.setOnRefreshListener {
            refresh()
        }


        //轮播图
        rotationMap.setAdapter(object : BannerImageAdapter<DataBean>(DataBean.testData3) {
            override fun onBindView(
                holder: BannerImageHolder,
                data: DataBean,
                position: Int,
                size: Int,
            ) {
                //图片加载自己实现
                Glide.with(holder.itemView)
                    .load(data.imageUrl)
                    .into(holder.imageView)
            }
        })
        rotationMap.addBannerLifecycleObserver(this).setIndicator(CircleIndicator(activity))
        rotationMap.setOnBannerListener(object : OnBannerListener<DataBean> {
            override fun OnBannerClick(data: DataBean?, position: Int) {
                Toast.makeText(activity, "${data?.imageUrl}", Toast.LENGTH_SHORT).show()
            }

        })

        //spinner下拉选项
        val adapter: SpinnerAdapter =
            ArrayAdapter<String>(requireActivity(), R.layout.my_spinner_layout, schoolList)
        schools.adapter = adapter
        schools.setSelection(0)  //设置默认选中项
        schools.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                Toast.makeText(activity, "选中了${schoolList[position]}", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }

        //跳转搜索页面
        search.setOnClickListener {
            val intent = Intent(activity, SearchActivity::class.java)
            startActivity(intent)
        }
        //跳转消息页面
        msg.setOnClickListener {
            val intent = Intent(activity, MessageActivity::class.java)
            startActivity(intent)
        }
        //功能入口
        func1.setOnClickListener {
            val intent = Intent(activity, LectureActivity::class.java)
            startActivity(intent)
        }
        func2.setOnClickListener {
            val intent = Intent(activity, InfosInSchoolActivity::class.java)
            startActivity(intent)
        }
        func3.setOnClickListener {
            val intent = Intent(activity, RewardTaskActivity::class.java)
            startActivity(intent)
        }
        func4.setOnClickListener {
            val intent = Intent(activity, RewardTaskActivity::class.java)
            startActivity(intent)
        }
        func5.setOnClickListener {
            val intent = Intent(activity, RewardTaskActivity::class.java)
            startActivity(intent)
        }
        func6.setOnClickListener {
            val intent = Intent(activity, RewardTaskActivity::class.java)
            startActivity(intent)
        }
        func7.setOnClickListener {
            val intent = Intent(activity, RewardTaskActivity::class.java)
            startActivity(intent)
        }
        func8.setOnClickListener {
            val intent = Intent(activity, RewardTaskActivity::class.java)
            startActivity(intent)
        }

        //校内资讯
        realTimeInfo.setOnClickListener {
            val intent = Intent(activity, InfosInSchoolActivity::class.java)
            startActivity(intent)
        }

        //讲座信息
        lecture.setOnClickListener {
            val intent = Intent(activity, LectureActivity::class.java)
            startActivity(intent)
        }

        return view
    }


    private fun initData() {
        //初始化校内资讯数据
        InformationInSchool.init(object : InformationInSchoolDataCallBack {
            override fun onFinish(response: ArrayList<InformationInSchool>) {
                if (response.size >= 1) {
                    activity?.runOnUiThread {
                        realTimeInfoContentText.setText("${response[0].time}  ${response[0].title}")
                        realTimeInfoContentText.setOnClickListener {
                            //Log.d("sgsg", "打开链接")
                            val uri: Uri = Uri.parse(response[0].url)
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            startActivity(intent)
                        }
                    }
                }
            }

            override fun onError(e: Exception) {
                e.printStackTrace()
            }

        })
        //初始化讲座信息
        Lecture.init(object : LectureDataCallBack {
            override fun onFinish(response: ArrayList<Lecture>) {
                if (response.size >= 1) {
                    activity?.runOnUiThread {
                        lectureContentTime.setText(response[0].time)
                        lectureContentTitle.setText(response[0].title)
                        lectureContent.setOnClickListener {
                            val intent =
                                Intent(context, LectureContentActivity::class.java).apply {
                                    putExtra("time", response[0].time)
                                    putExtra("title", response[0].title)
                                }
                            startActivity(intent)
                        }
                    }
                }
            }

            override fun onError(e: Exception) {
                e.printStackTrace()
            }

        })

    }

    private fun refresh() {
        //涉及刷新标志的消失，把两个请求串起来，虽然耗时长，但是鲁棒
        //刷新资讯信息
        InformationInSchool.init(object : InformationInSchoolDataCallBack {
            override fun onFinish(response: ArrayList<InformationInSchool>) {
                if (response.size >= 1) {
                    activity?.runOnUiThread {
                        realTimeInfoContentText.setText("${response[0].time}  ${response[0].title}")
                        realTimeInfoContentText.setOnClickListener {
                            //Log.d("sgsg", "打开链接")
                            val uri: Uri = Uri.parse(response[0].url)
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            startActivity(intent)
                        }
                    }
                }
                //刷新讲座信息
                Lecture.init(object : LectureDataCallBack {
                    override fun onFinish(response: ArrayList<Lecture>) {
                        if (response.size >= 1) {
                            activity?.runOnUiThread {
                                lectureContentTime.setText(response[0].time)
                                lectureContentTitle.setText(response[0].title)
                                lectureContent.setOnClickListener {
                                    val intent =
                                        Intent(context, LectureContentActivity::class.java).apply {
                                            putExtra("time", response[0].time)
                                            putExtra("title", response[0].title)
                                        }
                                    startActivity(intent)
                                }
                                refresh.isRefreshing = false
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


    private fun initView(view: View) {
        toolbar = view.toolbar
        schools = view.schools
        search = view.search
        msg = view.msg

        rotationMap = view.rotationMap as Banner<DataBean, BannerImageAdapter<DataBean>>

        func1 = view.func1
        func2 = view.func2
        func3 = view.func3
        func4 = view.func4
        func5 = view.func5
        func6 = view.func6
        func7 = view.func7
        func8 = view.func8

        realTimeInfoContentText = view.realTimeInfoContentText
        realTimeInfo = view.realTimeInfo

        lecture = view.lecture
        lectureContent = view.lectureContent
        lectureContentTitle = view.lectureContentTitle
        lectureContentTime = view.lectureContentTime

        refresh = view.refresh

    }

    private fun initSchools() {
        schoolList.add("武大")
        schoolList.add("华科")
        schoolList.add("武理")
    }

}
