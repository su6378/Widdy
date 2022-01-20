package com.example.widdy.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.widdy.R;
import com.example.widdy.begin.Begin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileEtc extends AppCompatActivity {

    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private TextView etc_profile_name;
    private ConstraintLayout etc_profile_manageLayout,etc_page,logoutBtn;
    private ImageView backBtn;

    private String nickname;

    //뒤로가기 버튼
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
    //닉네임 초기화
    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_etc_page);

        //하단바 색깔 적용
        getWindow().setNavigationBarColor(Color.parseColor("#282828"));

        //Firebase
        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //페이지
        etc_page = findViewById(R.id.etc_page);

        //프로필 이름
        etc_profile_name = findViewById(R.id.etc_profile_name);

        //초기 데이터 불러오기
        initData();

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

        //프로필 관리
        etc_profile_manageLayout = findViewById(R.id.etc_profile_manageLayout);

        //프로필 관리 페이지로 이동
        etc_profile_manageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileEtc.this,ProfileManage.class);
                intent.putExtra("nickname",nickname);
                startActivity(intent);
            }
        });

        //로그아웃
        logoutBtn = findViewById(R.id.logoutBtn);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ProfileEtc.this, Begin.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initData(){
        etc_page.setVisibility(View.INVISIBLE);
        //Firebase
        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //프로필 이름 불러오기
        fStore.collection("user").document(currentUser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        nickname = document.getString("nickname");
                        etc_profile_name.setText(nickname);
                        etc_page.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }
}