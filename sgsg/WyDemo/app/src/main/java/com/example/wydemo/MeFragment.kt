package com.example.wydemo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.me_fragment.view.*
import java.io.BufferedWriter
import java.io.IOException
import java.io.OutputStreamWriter
import android.widget.Toast

import android.content.DialogInterface


class MeFragment : Fragment() {
    private lateinit var title: TextView

    private lateinit var signBtn: LinearLayout
    private lateinit var signUp: Button
    private lateinit var signIn: Button

    private lateinit var userInfo: LinearLayout
    private lateinit var userAccount: TextView
    private lateinit var userCreditLevel: TextView
    private lateinit var userCertification: TextView
    private lateinit var userId: TextView

    private lateinit var exitBtn: Button
    private lateinit var proveBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.me_fragment, container, false)
        initViews(view)
        signUp.setOnClickListener {
            val intent = Intent(activity, SignUpActivity::class.java)
            startActivity(intent)
        }

        signIn.setOnClickListener {
            val intent = Intent(activity, SignInActivity::class.java)
            startActivity(intent)
        }
        exitBtn.setOnClickListener {
            val builder = AlertDialog.Builder(view.context)
            builder.setTitle("提示框")
            builder.setMessage("确定退出登录吗?")
            builder.setPositiveButton("确定") { dialog, which ->
                User.signIn = false
                activity?.let { it1 -> User.saveUserDataWithSharedPreferences(it1) }
                Toast.makeText(view.context, "退出登录", Toast.LENGTH_SHORT).show()
                refreshView()
            }
            builder.setNegativeButton("取消") { dialog, which ->
                Toast.makeText(view.context, "取消退出登录", Toast.LENGTH_SHORT).show()
            }
            builder.show()
        }
        proveBtn.setOnClickListener {
            val intent = Intent(activity, AnswerProveActivity::class.java)
            startActivity(intent)
        }
        return view
    }

    private fun initViews(view: View) {
        title = view.title

        signBtn = view.signBtn
        signUp = view.signUp
        signIn = view.signIn

        userInfo = view.userInfo
        userId = view.userId
        userAccount = view.userAccount
        userCertification = view.userCertification
        userCreditLevel = view.userCreditLevel

        exitBtn = view.exitBtn
        proveBtn = view.proveBtn
    }

    override fun onResume() {
        super.onResume()
        refreshView()
    }

    private fun refreshView() {
        if (User.signIn) {
            signBtn.visibility = View.GONE
            userInfo.visibility = View.VISIBLE
            exitBtn.visibility = View.VISIBLE
            userId.setText("id: ${User.userId}")
            userAccount.setText("账号: ${User.id}")
            userCreditLevel.setText("信用等级: ${User.creditLevel}")
            userCertification.setText("认证信息:${if (User.certification) "武汉大学" else "未认证"}")
        } else {
            signBtn.visibility = View.VISIBLE
            userInfo.visibility = View.GONE
            exitBtn.visibility = View.GONE
        }
        if (User.certification || (!User.signIn)) {
            proveBtn.visibility = View.GONE
        } else {
            proveBtn.visibility = View.VISIBLE
        }
    }

}
