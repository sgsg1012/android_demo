package com.example.wydemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_sign_up.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import java.util.regex.Matcher
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity() {
    private var relaAddress = "/user/account/register"
    private val args = HashMap<String, String>()
    private var VerificationCode: String = ""
    private var registered: Boolean = false
    private lateinit var myCountDownTimer: MyCountDownTimer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        //初始化
        //home键
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
        }
        //new倒计时对象,总共的时间,每隔多少秒更新一次时间
        myCountDownTimer = MyCountDownTimer(timerBtn, 60000, 1000)
        //注册逻辑

        timerBtn.setOnClickListener {
            val accountInput = account.text.toString()
            if (!isMobilePhone(accountInput)) {
                Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show()
            } else {
                isRegistered(accountInput)
            }
        }
        signUpBtn.setOnClickListener {
            val verificationCode = verificationCode.text.toString()
            if (verificationCode == "") {
                Toast.makeText(this, "未填写验证码", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (verificationCode == VerificationCode) {
                this.finish()
                val intent = Intent(this, SetPasswordActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "验证码不正确", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isRegistered(accountInput: String) {
        relaAddress = "/user/account/check"
        args.clear()
        args["openid"] = accountInput
        HttpUtil.sendRequestWithOkHttp(relaAddress, args, object : Callback {
            override fun onResponse(call: Call, response: Response) {
//                Log.d("sgsg", "发送请求成功")
                val responseData: String? = response.body()?.string()
                if (responseData != null) {
//                    Log.d("sgsg", responseData)
                    parseCheckData(responseData, accountInput)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
        })
    }

    private fun parseCheckData(data: String, accountInput: String) {
        try {
            val jsonObj = JSONObject(data)
            if (jsonObj.has("error")) {
                registered = true
            } else registered = false
            if (registered) {
                runOnUiThread { Toast.makeText(this, "该手机号已经注册", Toast.LENGTH_SHORT).show() }
            } else {
                getVetification(accountInput)
                runOnUiThread { myCountDownTimer.start() }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getVetification(accountInput: String) {
        relaAddress = "/user/account/captcha"
        args.clear()
        args["telephone"] = accountInput
        HttpUtil.sendRequestWithOkHttp(relaAddress, args, object : Callback {
            override fun onResponse(call: Call, response: Response) {
                Log.d("sgsg", "发送请求成功")
                val responseData: String? = response.body()?.string()
                if (responseData != null) {
                    //Log.d("sgsg", responseData)
                    parseVerificationCode(responseData)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }

    private fun isMobilePhone(s: String?): Boolean {
        val p: Pattern = Pattern.compile("^(13[0-9]|14[57]|15[0-35-9]|17[6-8]|18[0-9])[0-9]{8}$")
        val m: Matcher = p.matcher(s)
        return m.matches()
    }

    private fun parseVerificationCode(jsonData: String) {
        try {
            val jsonObj = JSONObject(jsonData)
            val data = jsonObj.getString("data")
            val jsonObj2 = JSONObject(data)
            User.id = jsonObj2.getString("phoneNumbers")
            VerificationCode = jsonObj2.getString("captcha")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun parseJSONWithJSONObject(jsonData: String): Boolean {
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
            User.id = jsonObj2.getString("userOpenid")
            User.pwd = jsonObj2.getString("userPassword")
            User.certification = if (jsonObj2.getString("certification") == "1") true else false
            User.creditLevel = jsonObj2.getString("creditLevel").toInt()
            User.saveUserDataWithSharedPreferences(applicationContext)
            runOnUiThread { Toast.makeText(applicationContext, "注册成功", Toast.LENGTH_SHORT).show() }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}


//signUpBtn.setOnClickListener {
//    val accountInput = account.text.toString()
//    val passwordInput = password.text.toString()
//    if (accountInput != "" && passwordInput != "") {
//        args["openid"] = accountInput
//        args["password"] = passwordInput
//        HttpUtil.sendRequestWithOkHttp(relaAddress, args, object : Callback {
//            override fun onResponse(call: Call, response: Response) {
//                //  Log.d("sgsg", "发送请求成功")
//                val responseData: String? = response.body()?.string()
//                if (responseData != null) {
//                    //   Log.d("sgsg", responseData)
//                    if (parseJSONWithJSONObject(responseData)) {
//                        Thread.sleep(1000)
//                        finish()
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call, e: IOException) {
//                e.printStackTrace()
//            }
//        })
//    } else Toast.makeText(this, "输入不能为空", Toast.LENGTH_SHORT).show()
//}