package com.android.virtus.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.virtus.chatapp.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class DangKyActivity extends AppCompatActivity {

    EditText editName, editEmail, editPassword, editRePassword;
    ProgressBar pbLoading;
    Button btnDK;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_ky);
        mAuth = FirebaseAuth.getInstance();
        editName = (EditText) findViewById(R.id.editName);
        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPassword);
        editRePassword = (EditText) findViewById(R.id.editRePassword);
        btnDK = (Button) findViewById(R.id.btnDK);
        pbLoading = (ProgressBar) findViewById(R.id.pbLoading);
        btnDK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DangKy();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {

        }
    }

    public void DangKy() {
        final String name = editName.getText().toString().trim();
        final String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String repassword = editRePassword.getText().toString().trim();
        if (!password.equals(repassword)) {
            Toast.makeText(DangKyActivity.this, "Mật khẩu không khớp!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (name.isEmpty()) {
            editName.setError("Bạn chưa nhập Họ tên");
            editName.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            editEmail.setError("Bạn chưa nhập Email");
            editEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            editPassword.setError("Bạn chưa nhập mật khẩu");
            editPassword.requestFocus();
            return;
        }
        if (repassword.isEmpty()) {
            editRePassword.setError("Bạn chưa xác nhận mật khẩu");
            editRePassword.requestFocus();
            return;
        }
        btnDK.setVisibility(View.GONE);
        pbLoading.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String currentId = mAuth.getCurrentUser().getUid();
                            User user = new User(
                                    currentId, name, email, "default", "online"
                            );
                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    pbLoading.setVisibility(View.GONE);
                                    btnDK.setVisibility(View.VISIBLE);
                                    if (task.isSuccessful()) {
                                        Toast.makeText(DangKyActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                                        Intent ItDangNhap = new Intent(DangKyActivity.this, DangNhapActivity.class);
                                        startActivity(ItDangNhap);
                                    }
                                }
                            });
                        } else {
                            pbLoading.setVisibility(View.GONE);
                            btnDK.setVisibility(View.VISIBLE);
                            Toast.makeText(DangKyActivity.this, "Đăng ký thất bại!\n " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
