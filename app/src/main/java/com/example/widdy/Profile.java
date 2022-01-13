package com.example.widdy;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.widdy.begin.Begin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Profile extends AppCompatActivity {

    private ImageView backBtn;
    private TextInputEditText login_email, login_password;
    private TextInputLayout login_emailLayout, login_passwordLayout;
    private Button loginBtn;
    private TextView move_register;
    private ProgressBar login_progress;
    private FirebaseAuth mAuth;
    FirebaseFirestore fStore;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Profile.this, Begin.class);
        startActivity(intent);
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
                Intent intent = new Intent(Profile.this, Begin.class);
                startActivity(intent);
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
                login_email.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B4B4B4")));
                login_password.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#505050")));
                login_emailLayout.setErrorEnabled(false);
                return false;
            }
        });

        login_password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                login_email.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#505050")));
                login_password.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B4B4B4")));
                login_passwordLayout.setErrorEnabled(false);
                return false;
            }
        });

        //로그인
        loginBtn = findViewById(R.id.loginBtn);
        login_progress = findViewById(R.id.login_progress);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loginBtn.setText("");
                login_progress.setVisibility(View.VISIBLE);

                String email = login_email.getText().toString();
                String password = login_password.getText().toString();


                signIn(email, password);


            }
        });


        //회원가입 창으로 이동
        move_register = findViewById(R.id.move_register);

        move_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile.this, Begin.class);
                startActivity(intent);
                finish();
            }
        });
    }

    //파이어베이스 로그인
    private void signIn(String email, String password) {

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {


                if (task.isSuccessful()) {

                    Intent intent = new Intent(Profile.this, Home.class);
                    startActivity(intent);
                    finish();

                } else {

                    loginBtn.setText("로그인");
                    login_progress.setVisibility(View.INVISIBLE);


                    fStore.collection("user").document(email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    login_passwordLayout.setError("비밀번호가 일치하지 않습니다.");
                                } else {
                                    login_emailLayout.setError("존재하지 않는 계정입니다.");
                                }
                            } else {

                            }
                        }
                    });

                }

            }
        });
    }


}