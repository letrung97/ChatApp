package com.android.virtus.chatapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.virtus.chatapp.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class DangKyActivity : AppCompatActivity() {
    var editName: EditText? = null
    var editEmail: EditText? = null
    var editPassword: EditText? = null
    var editRePassword: EditText? = null
    var pbLoading: ProgressBar? = null
    var btnDK: Button? = null
    var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dang_ky)
        mAuth = FirebaseAuth.getInstance()
        editName = findViewById<View>(R.id.editName) as EditText
        editEmail = findViewById<View>(R.id.editEmail) as EditText
        editPassword = findViewById<View>(R.id.editPassword) as EditText
        editRePassword = findViewById<View>(R.id.editRePassword) as EditText
        btnDK = findViewById<View>(R.id.btnDK) as Button
        pbLoading = findViewById<View>(R.id.pbLoading) as ProgressBar
        btnDK!!.setOnClickListener { DangKy() }
    }

    override fun onStart() {
        super.onStart()
        if (mAuth!!.currentUser != null) {
        }
    }

    fun DangKy() {
        val name = editName!!.text.toString().trim { it <= ' ' }
        val email = editEmail!!.text.toString().trim { it <= ' ' }
        val password = editPassword!!.text.toString().trim { it <= ' ' }
        val repassword = editRePassword!!.text.toString().trim { it <= ' ' }
        if (password != repassword) {
            Toast.makeText(this@DangKyActivity, "Mật khẩu không khớp!", Toast.LENGTH_SHORT).show()
            return
        }
        if (name.isEmpty()) {
            editName!!.error = "Bạn chưa nhập Họ tên"
            editName!!.requestFocus()
            return
        }
        if (email.isEmpty()) {
            editEmail!!.error = "Bạn chưa nhập Email"
            editEmail!!.requestFocus()
            return
        }
        if (password.isEmpty()) {
            editPassword!!.error = "Bạn chưa nhập mật khẩu"
            editPassword!!.requestFocus()
            return
        }
        if (repassword.isEmpty()) {
            editRePassword!!.error = "Bạn chưa xác nhận mật khẩu"
            editRePassword!!.requestFocus()
            return
        }
        btnDK!!.visibility = View.GONE
        pbLoading!!.visibility = View.VISIBLE
        mAuth!!.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val currentId = mAuth!!.currentUser!!.uid
                        val user = User(
                                currentId, name, email, "default", "online"
                        )
                        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().currentUser!!.uid)
                                .setValue(user).addOnCompleteListener { task ->
                                    pbLoading!!.visibility = View.GONE
                                    btnDK!!.visibility = View.VISIBLE
                                    if (task.isSuccessful) {
                                        Toast.makeText(this@DangKyActivity, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                                        val ItDangNhap = Intent(this@DangKyActivity, DangNhapActivity::class.java)
                                        startActivity(ItDangNhap)
                                    }
                                }
                    } else {
                        pbLoading!!.visibility = View.GONE
                        btnDK!!.visibility = View.VISIBLE
                        Toast.makeText(this@DangKyActivity, """Đăng ký thất bại!
 ${task.exception!!.message}""", Toast.LENGTH_SHORT).show()
                    }
                }
    }
}