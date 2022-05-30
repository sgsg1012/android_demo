package com.example.wydemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_set_password.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception

class SetPasswordActivity : AppCompatActivity() {
    private var show1 = false
    private var show2 = false
    private var relaAddress = "/user/account/register"
    private val args = HashMap<String, String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_password)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
        }
        showPwd1.setOnClickListener {
            if (!show1) {
                //明文
                pwd1.transformationMethod = HideReturnsTransformationMethod.getInstance()
                pwd1.setSelection(pwd1.text.length)
                showPwd1.setImageResource(R.drawable.eye_clicked)
                show1 = true
            } else {
                //密文
                pwd1.transformationMethod = PasswordTransformationMethod.getInstance()
                pwd1.setSelection(pwd1.text.length)
                showPwd1.setImageResource(R.drawable.eye)
                show1 = false
            }
        }
        showPwd2.setOnClickListener {
            if (!show2) {
                //明文
                pwd2.transformationMethod = HideReturnsTransformationMethod.getInstance()
                pwd2.setSelection(pwd2.text.length)
                showPwd2.setImageResource(R.drawable.eye_clicked)
                show2 = true
            } else {
                //密文
                pwd2.transformationMethod = PasswordTransformationMethod.getInstance()
                pwd2.setSelection(pwd2.text.length)
                showPwd2.setImageResource(R.drawable.eye)
                show2 = false
            }
        }
        pwdBtn.setOnClickListener {
            val p1 = pwd1.text.toString()
            val p2 = pwd2.text.toString()
            if (p1 != p2) {
                Toast.makeText(this, "两次密码不一致，请重新输入", Toast.LENGTH_SHORT).show()
            } else if (p1.length > 18 || p1.length < 6) {
                Toast.makeText(this, "密码长度不规范，请重新输入", Toast.LENGTH_SHORT).show()
            } else {
                register(p1)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }

    private fun register(pwd: String) {
        args.clear()
        if (User.id != null) args["openid"] = User.id!!
        args["password"] = pwd
        HttpUtil.sendRequestWithOkHttp(relaAddress, args, object : Callback {
            override fun onResponse(call: Call, response: Response) {
                //  Log.d("sgsg", "发送请求成功")
                val responseData: String? = response.body()?.string()
                if (responseData != null) {
                    //   Log.d("sgsg", responseData)
                    if (parseJSONWithJSONObject(responseData)) {
                        //登录
                        args.clear()
                        args["openid"] = User.id!!
                        args["password"] = pwd
                        HttpUtil.sendRequestWithOkHttp("/user/account/login",
                            args,
                            object : Callback {
                                override fun onResponse(call: Call, response: Response) {
//                        Log.d("sgsg", "发送请求成功")
                                    val responseData: String? = response.body()?.string()
                                    if (responseData != null) {
//                            Log.d("sgsg", responseData)
                                        if (signin(responseData)) {
                                            Thread.sleep(1000)
                                            finish()
                                        }
                                    }
                                }

                                override fun onFailure(call: Call, e: IOException) {
                                    e.printStackTrace()
                                }
                            })
                        finish()
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
        })
    }

    private fun parseJSONWithJSONObject(jsonData: String): Boolean {
        try {
            val jsonObj = JSONObject(jsonData)
            if (jsonObj.has("error")) {
                val msg = jsonObj.getString("message")
                runOnUiThread { Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show() }
                return false
            }
            runOnUiThread { Toast.makeText(applicationContext, "注册成功", Toast.LENGTH_SHORT).show() }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    private fun signin(jsonData: String): Boolean {
        try {
            val jsonObj = JSONObject(jsonData)
            if (jsonObj.has("error")) {
                val msg = jsonObj.getString("message")
                runOnUiThread { Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show() }
                return false
            }
            User.signIn = true
            val data = jsonObj.getString("data")
            val jsonObj2 = JSONObject(data)
            User.userId = jsonObj2.getString("userId").toInt()
            User.id = jsonObj2.getString("userOpenid")
            User.pwd = jsonObj2.getString("userPassword")
            User.certification = if (jsonObj2.getString("certification") == "1") true else false
            User.creditLevel = jsonObj2.getString("creditLevel").toInt()
            User.saveUserDataWithSharedPreferences(applicationContext)
            runOnUiThread { Toast.makeText(applicationContext, "登录成功", Toast.LENGTH_SHORT).show() }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}