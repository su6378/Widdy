package com.example.widdy.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.widdy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileManage extends AppCompatActivity {

    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private TextView profile_manage_nickname;
    private ImageView updateBtn,backBtn;
    private ConstraintLayout profile_manage_page;

    private String nickname;

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
        setContentView(R.layout.profile_manage_page);

        //Firebase
        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        Intent intent = getIntent();

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

        //프로필 페이지
        profile_manage_page = findViewById(R.id.profile_manage_page);
        //프로필 이름
        profile_manage_nickname = findViewById(R.id.profile_manage_nickname);

        //프로필 이름 불러오기
        initData();

        //프로필 클릭 시 프로필 수정 페이지로 이동
        updateBtn = findViewById(R.id.updateBtn);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileManage.this,ProfileUpdate.class);
                intent.putExtra("nickname",nickname);
                startActivity(intent);
            }
        });

    }
    //닉네임 초기화
    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initData(){
        profile_manage_page.setVisibility(View.INVISIBLE);

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
                        profile_manage_nickname.setText(nickname);
                        profile_manage_page.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }


}