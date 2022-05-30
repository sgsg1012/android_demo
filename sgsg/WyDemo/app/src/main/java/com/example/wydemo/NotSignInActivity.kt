package com.example.wydemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_not_sign_in.*

class NotSignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_not_sign_in)

        signIn.setOnClickListener {
            this.finish()
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
        signUp.setOnClickListener {
            this.finish()
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }


}