package com.example.widdy.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.widdy.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class ProfileCreate extends AppCompatActivity {

    private TextInputLayout profile_nameLayout;
    private TextInputEditText profile_name;
    private RadioButton rg_btn1,rg_btn2,rg_btn3;
    private AppCompatButton createBtn;
    private TextView radio_error;
    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    //뒤로가기 버튼
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_create_page);

        //하단바 색깔 적용
        getWindow().setNavigationBarColor(Color.parseColor("#282828"));

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        //프로필 이름
        profile_name = findViewById(R.id.profile_name);
        profile_nameLayout = findViewById(R.id.profile_nameLayout);

        ////클릭 시 레이아웃 배경색 변경
        profile_name.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                profile_nameLayout.setHint("");
                return false;
            }
        });

        //프로필 공백 시 에러
        profile_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                profile_nameLayout.setError("");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String name = profile_name.getText().toString().trim();
                if (name.isEmpty()) {

                    profile_nameLayout.setError("죄송합니다.이름은 반드시 입력하셔야 합니다.");
                }else{
                    profile_nameLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //표시할 컨텐츠
        rg_btn1 = findViewById(R.id.rg_btn1);
        rg_btn2 = findViewById(R.id.rg_btn2);
        rg_btn3 = findViewById(R.id.rg_btn3);
        radio_error = findViewById(R.id.radio_error);


        //프로필 생성
        createBtn = findViewById(R.id.createBtn);

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = profile_name.getText().toString().trim();
                if(name.isEmpty()){
                    profile_nameLayout.setError("죄송합니다.이름은 반드시 입력하셔야 합니다.");
                }else if(!rg_btn1.isChecked() && !rg_btn2.isChecked() && !rg_btn3.isChecked()){
                    radio_error.setVisibility(View.VISIBLE);
                }
                else{
                    //main content값 미리 넣기
                    DocumentReference documentReference1 = fStore.collection("user").document(currentUser.getEmail()).collection("movie")
                            .document("squid_game");

                    Map<String, Object> user1 = new HashMap<>();
                    user1.put("isPlay", false);

                    documentReference1.set(user1, SetOptions.merge());

                    //user 컬렉션에 프로필 데이터 삽입
                    DocumentReference documentReference = fStore.collection("user").document(currentUser.getEmail());

                    Map<String, Object> user = new HashMap<>();
                    user.put("nickname", name);
                    user.put("image","profile_icon.jpg");

                    documentReference.set(user, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        //프로필 페이지로 이동
                        public void onSuccess(Void unused) {
                            Intent intent = new Intent();
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
                }
            }
        });


    }

    private void initData() {
        /*profile_nameLayout.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));*/
    }
}