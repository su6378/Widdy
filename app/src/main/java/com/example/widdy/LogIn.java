package com.example.widdy;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LogIn extends AppCompatActivity {

    private ImageView backBtn;
    private TextInputEditText login_email, login_password;
    private TextInputLayout login_emailLayout, login_passwordLayout;
    private Button loginBtn;
    private TextView move_register;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in_page);

        //뒤로가기
        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        //아이디, 비밀번호

        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);
        login_emailLayout = findViewById(R.id.login_emailLayout);
        login_passwordLayout = findViewById(R.id.login_passwordLayout);

        ////클릭 시 레이아웃 배경색 변경
        login_email.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                login_emailLayout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B4B4B4")));
                login_passwordLayout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#505050")));
                return false;
            }
        });

        login_password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                login_emailLayout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#505050")));
                login_passwordLayout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B4B4B4")));
                return false;
            }
        });

        //로그인
        loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });



        //회원가입 창으로 이동
        move_register = findViewById(R.id.move_register);

        move_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }



}