package com.example.wydemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_up.account
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception

class SignInActivity : AppCompatActivity() {
    private var show = false
    private val relaAddress = "/user/account/login"
    private val args = HashMap<String, String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
        }
        //登录逻辑
        signInBtn.setOnClickListener {
            val accountInput = account.text.toString()
            val passwordInput = password.text.toString()
            if (accountInput != "" && passwordInput != "") {
                args["openid"] = accountInput
                args["password"] = passwordInput
                HttpUtil.sendRequestWithOkHttp(relaAddress, args, object : Callback {
                    override fun onResponse(call: Call, response: Response) {
//                        Log.d("sgsg", "发送请求成功")
                        val responseData: String? = response.body()?.string()
                        if (responseData != null) {
//                            Log.d("sgsg", responseData)
                            if (parseJSONWithJSONObject(responseData)) {
                                Thread.sleep(1000)
                                finish()
                            }
                        }
                    }

                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                    }
                })
            } else Toast.makeText(this, "输入不能为空", Toast.LENGTH_SHORT).show()
        }
        //密码
        showPwd.setOnClickListener {
            if (!show) {
                //明文
                password.transformationMethod = HideReturnsTransformationMethod.getInstance()
                password.setSelection(password.text.length)
                showPwd.setImageResource(R.drawable.eye_clicked)
                show = true
            } else {
                //密文
                password.transformationMethod = PasswordTransformationMethod.getInstance()
                password.setSelection(password.text.length)
                showPwd.setImageResource(R.drawable.eye)
                show = false
            }
        }

        //去注册
        goToSignUp.setOnClickListener {
            this.finish()
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
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