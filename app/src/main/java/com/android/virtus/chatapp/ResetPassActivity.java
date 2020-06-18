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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassActivity extends AppCompatActivity {

    EditText send_mail;
    Button btnResetPass;
    FirebaseAuth mAuth;
    ProgressBar pbLoadingRS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);
        send_mail = findViewById(R.id.send_email);
        btnResetPass = findViewById(R.id.btnResetPass);
        pbLoadingRS = findViewById(R.id.pbLoadingRS);
        mAuth = FirebaseAuth.getInstance();
        btnResetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = send_mail.getText().toString().trim();
                if (email.isEmpty()) {
                    Toast.makeText(ResetPassActivity.this, "Bạn cần nhập email khôi phục", Toast.LENGTH_SHORT).show();
                } else {
                    pbLoadingRS.setVisibility(View.VISIBLE);
                    btnResetPass.setVisibility(View.GONE);
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            pbLoadingRS.setVisibility(View.GONE);
                            btnResetPass.setVisibility(View.VISIBLE);
                            if (task.isSuccessful()) {
                                Toast.makeText(ResetPassActivity.this, "Gửi thành công, kiểm tra email của bạn", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ResetPassActivity.this, DangNhapActivity.class));
                            } else {
                                String err = task.getException().getMessage();
                                Toast.makeText(ResetPassActivity.this, err, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
