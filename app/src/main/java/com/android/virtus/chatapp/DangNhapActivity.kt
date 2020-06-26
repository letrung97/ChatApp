package com.android.virtus.chatapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class DangNhapActivity : AppCompatActivity() {
    var editEmailDN: TextView? = null
    var editPasswordDN: TextView? = null
    var btnDangKy: Button? = null
    var btnDangNhap: Button? = null
    var mAuth: FirebaseAuth? = null
    var firebaseUser: FirebaseUser? = null
    var pbLoading: ProgressBar? = null
    var forgot_pass: TextView? = null
    override fun onStart() {
        super.onStart()
        if (mAuth!!.currentUser != null) {
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val intent = Intent(this@DangNhapActivity, MainChatActivity::class.java)
        startActivity(intent)
        finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dang_nhap)
        mAuth = FirebaseAuth.getInstance()
        editEmailDN = findViewById<View>(R.id.editEmailDN) as EditText
        editPasswordDN = findViewById<View>(R.id.editPasswordDN) as EditText
        btnDangKy = findViewById<View>(R.id.btnDangKy) as Button
        btnDangNhap = findViewById<View>(R.id.btnDangNhap) as Button
        forgot_pass = findViewById(R.id.forget_pass)
        forgot_pass?.setOnClickListener({ startActivity(Intent(this@DangNhapActivity, ResetPassActivity::class.java)) })
        pbLoading = findViewById<View>(R.id.pbLoading) as ProgressBar
        btnDangKy!!.setOnClickListener {
            val itDangKy = Intent(this@DangNhapActivity, DangKyActivity::class.java)
            startActivity(itDangKy)
        }
        btnDangNhap?.setOnClickListener { DangNhap() }
    }

    fun DangNhap() {
        val email = editEmailDN!!.text.toString().trim { it <= ' ' }
        val password = editPasswordDN!!.text.toString().trim { it <= ' ' }
        if (email.isEmpty()) {
            editEmailDN!!.error = "Bạn chưa nhập email"
            editEmailDN!!.requestFocus()
            return
        }
        if (password.isEmpty()) {
            editPasswordDN!!.error = "Bạn chưa nhập mật khẩu"
            editPasswordDN!!.requestFocus()
            return
        }
        btnDangNhap!!.visibility = View.GONE
        pbLoading!!.visibility = View.VISIBLE
        mAuth!!.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    pbLoading!!.visibility = View.GONE
                    btnDangNhap!!.visibility = View.VISIBLE
                    if (task.isSuccessful) {
                        Toast.makeText(this@DangNhapActivity, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                        val ItMainChat = Intent(this@DangNhapActivity, MainChatActivity::class.java)
                        startActivity(ItMainChat)
                    } else {
                        Toast.makeText(this@DangNhapActivity, "Đăng nhập thất bại!", Toast.LENGTH_SHORT).show()
                    }
                }
    }
}