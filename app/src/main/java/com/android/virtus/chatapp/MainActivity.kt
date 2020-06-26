package com.android.virtus.chatapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Handler().postDelayed({
            val itDangNhap = Intent(this@MainActivity, DangNhapActivity::class.java)
            startActivity(itDangNhap)
            finish()
        }, DELAY_TIME.toLong())
    }

    companion object {
        var DELAY_TIME = 1000
    }
}