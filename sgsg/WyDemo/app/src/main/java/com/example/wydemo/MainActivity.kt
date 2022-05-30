package com.example.wydemo

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.MenuItem
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.me_fragment.*
import java.io.BufferedWriter
import java.io.IOException
import java.io.OutputStreamWriter

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //初始化User数据
        User.loadUserDataWithSharedPreferences(applicationContext)
        if (!User.signIn) {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
        //初始化fragment
        replaceFragment(HomeFragment())
        homeImage.setImageResource(R.drawable.home_clicked)
        homeText.setTextColor(getResources().getColor(R.color.teal_200))
        //绑定点击事件 改变 fragment
        home.setOnClickListener {
            if (homeText.currentTextColor != getResources().getColor(R.color.teal_200)) {
                replaceFragment(HomeFragment())
                homeImage.setImageResource(R.drawable.home_clicked)
                meImage.setImageResource(R.drawable.me)
                homeText.setTextColor(getResources().getColor(R.color.teal_200))
                meText.setTextColor(getResources().getColor(R.color.black))
            }
        }
        me.setOnClickListener {
            if (meText.currentTextColor != getResources().getColor(R.color.teal_200)) {
                replaceFragment(MeFragment())
                homeImage.setImageResource(R.drawable.home)
                meImage.setImageResource(R.drawable.me_clicked)
                homeText.setTextColor(getResources().getColor(R.color.black))
                meText.setTextColor(getResources().getColor(R.color.teal_200))
            }
        }
        // 跳到发布页面
        addBtn.setOnClickListener {
            val intent = Intent(this, PublishActivity::class.java)
            startActivity(intent)
        }
    }


    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.commit()
    }
}