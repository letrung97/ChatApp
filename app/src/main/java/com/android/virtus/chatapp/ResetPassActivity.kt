package com.android.virtus.chatapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ResetPassActivity : AppCompatActivity() {
    var send_mail: EditText? = null
    var btnResetPass: Button? = null
    var mAuth: FirebaseAuth? = null
    var pbLoadingRS: ProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_pass)
        send_mail = findViewById(R.id.send_email)
        btnResetPass = findViewById(R.id.btnResetPass)
        pbLoadingRS = findViewById(R.id.pbLoadingRS)
        mAuth = FirebaseAuth.getInstance()
        btnResetPass?.setOnClickListener {
            val email = send_mail?.getText().toString().trim { it <= ' ' }
            if (email.isEmpty()) {
                Toast.makeText(this@ResetPassActivity, "Bạn cần nhập email khôi phục", Toast.LENGTH_SHORT).show()
            } else {
                pbLoadingRS?.setVisibility(View.VISIBLE)
                btnResetPass?.setVisibility(View.GONE)
                mAuth?.sendPasswordResetEmail(email)!!.addOnCompleteListener { task ->
                    pbLoadingRS?.setVisibility(View.GONE)
                    btnResetPass?.setVisibility(View.VISIBLE)
                    if (task.isSuccessful) {
                        Toast.makeText(this@ResetPassActivity, "Gửi thành công, kiểm tra email của bạn", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@ResetPassActivity, DangNhapActivity::class.java))
                    } else {
                        val err = task.exception!!.message
                        Toast.makeText(this@ResetPassActivity, err, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}