package com.example.wydemo

import android.os.CountDownTimer
import android.widget.Button

//倒计时函数
class MyCountDownTimer(
    val timeButton: Button,
    val millisInFuture: Long,
    val countDownInterval: Long,
) :
    CountDownTimer(millisInFuture, countDownInterval) {

    //计时过程
    override fun onTick(l: Long) {
        //防止计时过程中重复点击
        timeButton.setClickable(false)
        timeButton.setText("${l / 1000} 秒")
    }

    //计时完毕的方法
    override fun onFinish() {
        //重新给Button设置文字
        timeButton.setText("重新获取")
        //设置可点击
        timeButton.setClickable(true)
    }

}