package com.example.wydemo

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_lecture_content.*
import kotlinx.android.synthetic.main.activity_publish.*
import kotlinx.android.synthetic.main.get_input.view.*
import kotlinx.android.synthetic.main.image_dialog.view.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class PublishActivity : AppCompatActivity() {
    //dialog
    private lateinit var imgDialog: Dialog
    private lateinit var tagDialog: Dialog


    //图片放大显示
    private lateinit var dialog: Dialog
    private lateinit var image: ImageView

    //创建任务请求参数
    private val args = HashMap<String, String>()
    private val arrayArgs = HashMap<String, ArrayList<String>>()

    //选择分类
    private var select = false
    private var type: String = "lostProperty"

    //上传图片的url列表
    private val imgUrls = ArrayList<String>()

    //tag列表
    private val tagList = ArrayList<String>()
    private var tagText1 = ""
    private var tagText2 = ""
    private var tagText3 = ""
    private var tag_num = 0

    //recyclerView的数据
    private val bitmaps = ArrayList<UploadImage>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_publish)

        if (!User.signIn) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("提示框")
            builder.setMessage("您还未登录，登录后才能发布任务")
            builder.setPositiveButton("确定") { dialog, which ->
                this.finish()
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
            }
            builder.show()
        }
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
        }

        //图片全屏显示
        init()
        //展示上传的图片
        val layoutManager = LinearLayoutManager(applicationContext)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        imgRecyclerView.layoutManager = layoutManager
        val adapter = UploadImageAdapter(bitmaps, object : OnClickCallback2 {
            override fun onClick(view: View, position: Int, type: Int) {
                if (type == 0) {
                    //放大
                    image.setImageBitmap(bitmaps[position].bitmap)
                    dialog.show()
                } else {
                    //remove
                    bitmaps.removeAt(position)
                    imgUrls.removeAt(position)
                    imgRecyclerView.adapter?.notifyDataSetChanged()
                }
            }
        })
        imgRecyclerView.adapter = adapter

        //上传图片按钮
        addImage.setOnClickListener {
            imgDialog = Dialog(this, R.style.JumpDialog)
            val layout = LayoutInflater.from(this)
                .inflate(R.layout.image_dialog, null, false)
            val camera = layout.text_camera
            val dcim = layout.text_dcim
            val cancel = layout.text_cancel
            camera.setOnClickListener {
                Log.d("sgsg", "camera")
                getImage.getCamera(this)
                imgDialog.dismiss()
            }
            dcim.setOnClickListener {
                Log.d("sgsg", "dcim")
                getImage.getDcim(this)
                imgDialog.dismiss()
            }
            cancel.setOnClickListener {
                Log.d("sgsg", "cancel")
                imgDialog.dismiss()
            }
            imgDialog.setContentView(layout) //将视图加入容器
            val dialogWindow: Window? = imgDialog.getWindow() //获得窗口
            dialogWindow?.setGravity(Gravity.BOTTOM) //放置在底部

            val lp: WindowManager.LayoutParams? = dialogWindow?.getAttributes() // 获取对话框当前的参数值

            lp?.x = 0 // 新位置X坐标
            lp?.y = 0 // 新位置Y坐标
            lp?.width = this.getResources().getDisplayMetrics().widthPixels // 宽度
            layout.measure(0, 0)
            lp?.height = layout.getMeasuredHeight()
            lp?.alpha = 1f // 透明度

            dialogWindow?.setAttributes(lp)
            //点击空白消失
            //imgDialog.setCancelable(false)
            imgDialog.show()
        }

        //tag
        initTagBtn()

        //选择分类
        initSelectType()

        //发布任务
        //前端更改参数
        //location用来存储创建时间
        //有用的参数
        //title content contactNumber location(time) tags(未实现) picture openid
        publish.setOnClickListener {
            args.clear()
            arrayArgs.clear()
            tagList.clear()
            if (imgUrls.size == 0) imgUrls.add("null")
            if (tagText1 != "") tagList.add(tagText1)
            if (tagText2 != "") tagList.add(tagText2)
            if (tagText3 != "") tagList.add(tagText3)
            if (tagList.size == 0) tagList.add("null")

            if (!User.signIn) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("提示框")
                builder.setMessage("您还未登录，登录后才能发布任务")
                builder.setPositiveButton("确定") { dialog, which ->
                    this.finish()
                    val intent = Intent(this, SignInActivity::class.java)
                    startActivity(intent)
                }
                builder.show()
                return@setOnClickListener
            }
            val title = titleInput.text.toString()
            if (title == "") {
                Toast.makeText(this, "创建失败,标题不能为空", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val contactNumber = contact.text.toString()
            if (contactNumber == "") {
                Toast.makeText(this, "创建失败,联系方式不能为空", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val content = content.text.toString()
            if (content == "") {
                Toast.makeText(this, "创建失败,内容不能为空", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!select) {
                Toast.makeText(this, "创建失败,请选择分类", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            args["contactNumber"] = contactNumber
            args["content"] = content
            args["location"] = getNow()//发布时间
            args["openid"] = User.id!!
            arrayArgs["picture"] = imgUrls
            arrayArgs["tags"] = tagList
            args["title"] = title

            //凑数参数
            args["hyperlink"] = "hyperlink"
            args["amount"] = "1"
            args["gender"] = "1"
            args["presenter"] = "1"
            Task.createTask(type, args, object : createTaskCallBack {
                override fun onSuccess(response: String) {
                    val jsonObj = JSONObject(response)
                    val code = jsonObj.getInt("code")
                    if (code == 0) {
                        runOnUiThread {
                            Toast.makeText(applicationContext, "创建成功", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        val msg = jsonObj.getString("message")
                        runOnUiThread {
                            Toast.makeText(applicationContext,
                                "创建失败," + msg,
                                Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    Thread.sleep(1000)
                    finish()
                }

            }, arrayArgs)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            getImage.takePhoto -> {
                if (resultCode == Activity.RESULT_OK) {
                    // 将拍摄的照片显示出来
                    var bitmap =
                        BitmapFactory.decodeStream(contentResolver.openInputStream(getImage.imageUri))
                    bitmap = rotateIfRequired(bitmap)
                    bitmaps.add(UploadImage(bitmap))
                    imgRecyclerView.adapter?.notifyDataSetChanged()
                    //压缩图片
                    val path = getImage.outputImage.path
                    Task.compressPicture(bitmap, path)
                    //上传图片
//                    Log.d("sgsg", path)
                    User.id?.let {
                        Task.uploadImg(it, path, object : Callback {
                            override fun onFailure(call: Call, e: IOException) {
                                e.printStackTrace()
                            }

                            override fun onResponse(call: Call, response: Response) {
                                val responseData: String? = response.body()?.string()
                                if (responseData != null) {
                                    val jsonObj = JSONObject(responseData)
                                    val url = jsonObj.getString("data")
                                    Log.d("sgsg", url)
                                    imgUrls.add(url)
                                }
                            }
                        })
                    }

                }
            }
            getImage.fromAlbum -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    data.data?.let { uri ->
                        // 将选择的图片显示
                        var bitmap = getBitmapFromUri(uri)
//                        bitmap = rotateIfRequired(bitmap!!)
                        bitmap?.let { UploadImage(it) }?.let { bitmaps.add(it) }
                        imgRecyclerView.adapter?.notifyDataSetChanged()
                        val path = UriUtils.getFileAbsolutePath(this, uri)
                        //压缩图片
                        if (bitmap != null) {
                            if (path != null) {
                                Task.compressPicture(bitmap!!, path)
                            }
                        }
                        //上传图片
                        if (path != null) {
                            User.id?.let {
                                Task.uploadImg(it, path, object : Callback {
                                    override fun onFailure(call: Call, e: IOException) {
                                        e.printStackTrace()
                                    }

                                    override fun onResponse(call: Call, response: Response) {
                                        val responseData: String? = response.body()?.string()
                                        if (responseData != null) {
                                            val jsonObj = JSONObject(responseData)
                                            val url = jsonObj.getString("data")
                                            Log.d("sgsg", url)
                                            imgUrls.add(url)
                                        }
                                    }
                                })
                            }
                        } else Log.d("sgsg", "path_error")

                    }
                }

            }
        }
    }

    private fun getBitmapFromUri(uri: Uri) = contentResolver
        .openFileDescriptor(uri, "r")?.use {
            BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
        }


    private fun rotateIfRequired(bitmap: Bitmap): Bitmap {
        val exif = ExifInterface(getImage.outputImage.path)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL)
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270)
            else -> bitmap
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, degree: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height,
            matrix, true)
        bitmap.recycle() // 将不再需要的Bitmap对象回收
        return rotatedBitmap
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }

    //放大
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

    private fun initSelectType() {
        type1.setTextColor(getResources().getColor(R.color.black))
        type2.setTextColor(getResources().getColor(R.color.black))
        type3.setTextColor(getResources().getColor(R.color.black))
        type4.setTextColor(getResources().getColor(R.color.black))
        type5.setTextColor(getResources().getColor(R.color.black))
        type6.setTextColor(getResources().getColor(R.color.black))
        type7.setTextColor(getResources().getColor(R.color.black))
        type8.setTextColor(getResources().getColor(R.color.black))

        type1.setOnClickListener {
            Toast.makeText(this, "讲座项目不能私人发布，请与工作人员联系", Toast.LENGTH_SHORT).show()
            return@setOnClickListener
            select = true
            type1.setTextColor(getResources().getColor(R.color.teal_200))
            type2.setTextColor(getResources().getColor(R.color.black))
            type3.setTextColor(getResources().getColor(R.color.black))
            type4.setTextColor(getResources().getColor(R.color.black))
            type5.setTextColor(getResources().getColor(R.color.black))
            type6.setTextColor(getResources().getColor(R.color.black))
            type7.setTextColor(getResources().getColor(R.color.black))
            type8.setTextColor(getResources().getColor(R.color.black))
            type = "lecture"
            Toast.makeText(this, "已选择讲座信息", Toast.LENGTH_SHORT).show()
        }
        type2.setOnClickListener {
            select = true
            type1.setTextColor(getResources().getColor(R.color.black))
            type2.setTextColor(getResources().getColor(R.color.teal_200))
            type3.setTextColor(getResources().getColor(R.color.black))
            type4.setTextColor(getResources().getColor(R.color.black))
            type5.setTextColor(getResources().getColor(R.color.black))
            type6.setTextColor(getResources().getColor(R.color.black))
            type7.setTextColor(getResources().getColor(R.color.black))
            type8.setTextColor(getResources().getColor(R.color.black))
            type = "purchasing"
            Toast.makeText(this, "已选择校内跑腿", Toast.LENGTH_SHORT).show()
        }
        type3.setOnClickListener {
            select = true
            type1.setTextColor(getResources().getColor(R.color.black))
            type2.setTextColor(getResources().getColor(R.color.black))
            type3.setTextColor(getResources().getColor(R.color.teal_200))
            type4.setTextColor(getResources().getColor(R.color.black))
            type5.setTextColor(getResources().getColor(R.color.black))
            type6.setTextColor(getResources().getColor(R.color.black))
            type7.setTextColor(getResources().getColor(R.color.black))
            type8.setTextColor(getResources().getColor(R.color.black))
            type = "reward"
            Toast.makeText(this, "已选择悬赏求助", Toast.LENGTH_SHORT).show()
        }
        type4.setOnClickListener {
            select = true
            type1.setTextColor(getResources().getColor(R.color.black))
            type2.setTextColor(getResources().getColor(R.color.black))
            type3.setTextColor(getResources().getColor(R.color.black))
            type4.setTextColor(getResources().getColor(R.color.teal_200))
            type5.setTextColor(getResources().getColor(R.color.black))
            type6.setTextColor(getResources().getColor(R.color.black))
            type7.setTextColor(getResources().getColor(R.color.black))
            type8.setTextColor(getResources().getColor(R.color.black))
            type = "study"
            Toast.makeText(this, "已选择学习资源", Toast.LENGTH_SHORT).show()
        }
        //互助==悬赏 。。。
        type5.setOnClickListener {
            select = true
            type1.setTextColor(getResources().getColor(R.color.black))
            type2.setTextColor(getResources().getColor(R.color.black))
            type3.setTextColor(getResources().getColor(R.color.black))
            type4.setTextColor(getResources().getColor(R.color.black))
            type5.setTextColor(getResources().getColor(R.color.teal_200))
            type6.setTextColor(getResources().getColor(R.color.black))
            type7.setTextColor(getResources().getColor(R.color.black))
            type8.setTextColor(getResources().getColor(R.color.black))
            type = "reward"
            Toast.makeText(this, "已选择互助任务", Toast.LENGTH_SHORT).show()
        }
        type6.setOnClickListener {
            select = true
            type1.setTextColor(getResources().getColor(R.color.black))
            type2.setTextColor(getResources().getColor(R.color.black))
            type3.setTextColor(getResources().getColor(R.color.black))
            type4.setTextColor(getResources().getColor(R.color.black))
            type5.setTextColor(getResources().getColor(R.color.black))
            type6.setTextColor(getResources().getColor(R.color.teal_200))
            type7.setTextColor(getResources().getColor(R.color.black))
            type8.setTextColor(getResources().getColor(R.color.black))
            type = "lostProperty"
            Toast.makeText(this, "已选择失物招领", Toast.LENGTH_SHORT).show()
        }
        type7.setOnClickListener {
            select = true
            type1.setTextColor(getResources().getColor(R.color.black))
            type2.setTextColor(getResources().getColor(R.color.black))
            type3.setTextColor(getResources().getColor(R.color.black))
            type4.setTextColor(getResources().getColor(R.color.black))
            type5.setTextColor(getResources().getColor(R.color.black))
            type6.setTextColor(getResources().getColor(R.color.black))
            type7.setTextColor(getResources().getColor(R.color.teal_200))
            type8.setTextColor(getResources().getColor(R.color.black))
            type = "idle"
            Toast.makeText(this, "已选择闲置二手", Toast.LENGTH_SHORT).show()
        }
        type8.setOnClickListener {
            select = true
            type1.setTextColor(getResources().getColor(R.color.black))
            type2.setTextColor(getResources().getColor(R.color.black))
            type3.setTextColor(getResources().getColor(R.color.black))
            type4.setTextColor(getResources().getColor(R.color.black))
            type5.setTextColor(getResources().getColor(R.color.black))
            type6.setTextColor(getResources().getColor(R.color.black))
            type7.setTextColor(getResources().getColor(R.color.black))
            type8.setTextColor(getResources().getColor(R.color.teal_200))
            type = "jobs"
            Toast.makeText(this, "已选择就业实习", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Cofox 日期函数
     * created at 2017/12/19 0:06
     * 功能描述：返回当前日期，格式：2017-12-19 12:13:55
     * file:cofoxFuction.kt
     *
     *
     * 修改历史：
     * 2017/12/19:新建
     *
     */
    private fun getNow(): String {
        if (android.os.Build.VERSION.SDK_INT >= 24) {
            return SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        } else {
            var tms = Calendar.getInstance()
            return tms.get(Calendar.YEAR).toString() + "-" + tms.get(Calendar.MONTH)
                .toString() + "-" + tms.get(Calendar.DAY_OF_MONTH).toString() + " " + tms.get(
                Calendar.HOUR_OF_DAY).toString() + ":" + tms.get(Calendar.MINUTE)
                .toString() + ":" + tms.get(Calendar.SECOND)
                .toString() + "." + tms.get(Calendar.MILLISECOND).toString()
        }
    }

    private fun initTagBtn() {
        tag1.visibility = View.GONE
        tag2.visibility = View.GONE
        tag3.visibility = View.GONE
        tag.setOnClickListener {
            if (tag_num >= 3) {
                Toast.makeText(this, "最多三个标签", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            tagDialog = Dialog(this, R.style.JumpDialog)
            val layout = LayoutInflater.from(this)
                .inflate(R.layout.get_input, null, false)
            val input = layout.input
            val cancel = layout.cancel
            val confirm = layout.confirm
            confirm.setOnClickListener {
                val text = input.text.toString()
                if (text == "") {
                    Toast.makeText(this, "内容为空", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                tag_num = tag_num + 1
                if (tagText1 == "") {
                    tagText1 = text
                    tag1.visibility = View.VISIBLE
                    tag1Text.text = tagText1
                } else if (tagText2 == "") {
                    tagText2 = text
                    tag2.visibility = View.VISIBLE
                    tag2Text.text = tagText2
                } else {
                    tagText3 = text
                    tag3.visibility = View.VISIBLE
                    tag3Text.text = tagText3
                }
                tagDialog.dismiss()
            }
            cancel.setOnClickListener {
                tagDialog.dismiss()
            }
            tagDialog.setContentView(layout) //将视图加入容器
            val dialogWindow: Window? = tagDialog.getWindow() //获得窗口
            dialogWindow?.setGravity(Gravity.CENTER) //放置在底部

            val lp: WindowManager.LayoutParams? = dialogWindow?.getAttributes() // 获取对话框当前的参数值

            lp?.x = 0 // 新位置X坐标
            lp?.y = 0 // 新位置Y坐标
            lp?.width = this.getResources().getDisplayMetrics().widthPixels // 宽度
            layout.measure(0, 0)
            lp?.height = layout.getMeasuredHeight()
            lp?.alpha = 1f // 透明度

            dialogWindow?.setAttributes(lp)
            //点击空白消失
            //imgDialog.setCancelable(false)
            tagDialog.show()
        }
        tag1Text.setOnClickListener {
            tagText1 = ""
            tag1.visibility = View.GONE
            tag_num = tag_num - 1
        }
        tag2Text.setOnClickListener {
            tagText2 = ""
            tag2.visibility = View.GONE
            tag_num = tag_num - 1
        }
        tag3Text.setOnClickListener {
            tagText3 = ""
            tag3.visibility = View.GONE
            tag_num = tag_num - 1
        }
    }


}