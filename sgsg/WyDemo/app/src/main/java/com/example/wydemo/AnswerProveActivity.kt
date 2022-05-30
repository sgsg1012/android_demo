package com.example.wydemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_answer_prove.*

class AnswerProveActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_answer_prove)
        //init
        questionContent.setText("信息学部主教学楼大家都叫什么楼？")
        answer1.setText("一楼")
        answer2.setText("青楼")

        answer1.setOnClickListener {
            answer1.setTextColor(getResources().getColor(R.color.teal_200))
            answer2.setTextColor(getResources().getColor(R.color.black))
            answer1.setClickable(false)
            answer2.setClickable(true)
        }
        answer2.setOnClickListener {
            answer2.setTextColor(getResources().getColor(R.color.teal_200))
            answer1.setTextColor(getResources().getColor(R.color.black))
            answer2.setClickable(false)
            answer1.setClickable(true)
        }
        submit.setOnClickListener {
            if (answer2.currentTextColor == getResources().getColor(R.color.teal_200)) {
                Toast.makeText(this, "答案正确,认证成功", Toast.LENGTH_SHORT).show()
                User.certification = true
                User.saveUserDataWithSharedPreferences(this)
                finish()
            } else {
                Toast.makeText(this, "答案错误", Toast.LENGTH_SHORT).show()
            }
        }
    }
}