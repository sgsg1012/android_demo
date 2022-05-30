package com.example.wydemo

import android.content.Context
import android.util.Log
import java.io.BufferedWriter
import java.io.IOException
import java.io.OutputStreamWriter

object User {

    var signIn: Boolean = false
    var id: String? = "xxx"
    var userId: Int = 0
    var pwd: String? = "xxx"
    var certification: Boolean = false
    var creditLevel: Int = 1

    fun saveUserDataWithSharedPreferences(context: Context) {
        val editor = context.getSharedPreferences("data", Context.MODE_PRIVATE).edit()
        editor.putBoolean("SignIn", User.signIn)
        editor.putString("id", User.id)
        editor.putString("password", User.pwd)
        editor.putBoolean("certification", User.certification)
        editor.putInt("creditLevel", User.creditLevel)
        editor.putInt("userId", User.userId)
        editor.apply()
    }

    fun loadUserDataWithSharedPreferences(context: Context) {
        val data = context.getSharedPreferences("data", Context.MODE_PRIVATE)
        User.signIn = data.getBoolean("SignIn", false)
        User.id = data.getString("id", "")
        User.pwd = data.getString("password", "")
        User.certification = data.getBoolean("certification", false)
        User.creditLevel = data.getInt("creditLevel", 1)
        User.userId = data.getInt("userId", 0)
    }

    fun saveUserDataWithFile(context: Context) {
        Log.d("sgsg", "save")
        try {
            val output = context.openFileOutput("data", Context.MODE_PRIVATE)
            val writer = BufferedWriter(OutputStreamWriter(output))
            writer.use {
                if (User.signIn) {
                    it.write("1\n${User.id}\n${User.pwd}\n" +
                            "${User.creditLevel}\n${if (User.certification) 1 else 0}")
                    Log.d("sgsg", "write")
                } else {
                    it.write("0")
                    Log.d("sgsg", "write")
                }
            }
        } catch (e: IOException) {
            Log.d("sgsg", "error")
            e.printStackTrace()
        }

    }
}