package com.example.widdy.profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.widdy.Main;
import com.example.widdy.R;
import com.example.widdy.begin.Begin;
import com.example.widdy.onBackPressedListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {
    private long lastTimeBackPressed;
    private Button logoutBtn;
    private MaterialCardView nextBtn;
    private TextView profile_nickname, profile_text, profile_logo;
    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private ConstraintLayout profile_page;
    private ProgressBar profile_progress;
    private CircleImageView add_profile;
    private ImageView profile_image, profile_update;
    private ConstraintLayout profile_updateLayout;

    //뒤로가기 버튼
    @Override
    public void onBackPressed() {

        //프래그먼트 onBackPressedListener사용
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragmentList) {
            if (fragment instanceof onBackPressedListener) {
                ((onBackPressedListener) fragment).onBackPressed();
                return;
            }
        }

        //두 번 클릭시 어플 종료
        if (System.currentTimeMillis() - lastTimeBackPressed < 1500) {
            finish();
            return;
        }
        lastTimeBackPressed = System.currentTimeMillis();
        Toast.makeText(this, "'뒤로' 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();

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
        setContentView(R.layout.profile_page);

        //하단바 색깔 적용
        getWindow().setNavigationBarColor(Color.parseColor("#282828"));


        //프로그레스바
        profile_progress = findViewById(R.id.profile_progress);
        profile_page = findViewById(R.id.profile_page);

        //프로필
        add_profile = findViewById(R.id.add_profile);
        profile_image = findViewById(R.id.profile_image);
        profile_nickname = findViewById(R.id.profile_nickname);

        //로그아웃
        logoutBtn = findViewById(R.id.logoutBtn);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(Profile.this, Begin.class);
                startActivity(intent);
                finish();
            }
        });

        //프로필 생성 or 변경 or 메인 페이지 이동
        nextBtn = findViewById(R.id.nextBtn);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                profileExist();
            }
        });

        //프로필 변경
        profile_update = findViewById(R.id.profile_update);
        profile_updateLayout = findViewById(R.id.profile_updateLayout);
        profile_text = findViewById(R.id.profile_text);
        profile_logo = findViewById(R.id.profile_logo);

        profile_updateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fStore.collection("user").document(currentUser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String nickname = document.getString("nickname");
                                if (nickname == null) {

                                } else {
                                    //프로필 관리 창 띄우기

                                    profile_text.setVisibility(View.INVISIBLE);
                                    profile_logo.setVisibility(View.INVISIBLE);
                                    profile_update.setVisibility(View.INVISIBLE);
                                    nextBtn.setEnabled(false);


                                    //레이아웃을 위에 겹쳐서 올리는 부분
                                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                                    //레이아웃 객체생성
                                    ConstraintLayout ll = (ConstraintLayout) inflater.inflate(R.layout.profile_manage_page, null);

                                    //레이아웃 배경 투명도 주기
                                    ll.setBackgroundColor(Color.parseColor("#99000000"));

                                    //레이아웃 위에 겹치기
                                    ConstraintLayout.LayoutParams paramll = new ConstraintLayout.LayoutParams
                                            (ConstraintLayout.LayoutParams.FILL_PARENT, ConstraintLayout.LayoutParams.FILL_PARENT);
                                    addContentView(ll, paramll);

                                    ImageView backBtn = ll.findViewById(R.id.backBtn);

                                    backBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            //중첩 레이아웃 삭제하기
                                            ((ViewManager) ll.getParent()).removeView(ll);
                                            profile_text.setVisibility(View.VISIBLE);
                                            profile_logo.setVisibility(View.VISIBLE);
                                            profile_update.setVisibility(View.VISIBLE);
                                            nextBtn.setEnabled(true);
                                        }
                                    });

                                    //회원정보 수정 페이지로 이동
                                    ImageView updateBtn = ll.findViewById(R.id.updateBtn);
                                    updateBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            //중첩 레이아웃 삭제하기
                                            ((ViewManager) ll.getParent()).removeView(ll);
                                            profile_text.setVisibility(View.VISIBLE);
                                            profile_logo.setVisibility(View.VISIBLE);
                                            profile_update.setVisibility(View.VISIBLE);
                                            nextBtn.setEnabled(true);
                                            Intent intent = new Intent(Profile.this, ProfileUpdate.class);
                                            intent.putExtra("nickname", nickname);
                                            startActivity(intent);

                                        }
                                    });
                                }
                            }
                        }
                    }
                });
            }
        });


    }

    private void initData() {

        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //progressbar 띄우기
        profile_page.setVisibility(View.INVISIBLE);
        profile_progress.setVisibility(View.VISIBLE);


        fStore.collection("user").document(currentUser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String nickname = document.getString("nickname");
                        String image = document.getString("image");
                        if (document.getString("nickname") == null) {
                            profile_nickname.setText("프로필 추가");
                            profile_page.setVisibility(View.VISIBLE);
                            profile_progress.setVisibility(View.INVISIBLE);
                            add_profile.setVisibility(View.VISIBLE);
                            profile_image.setVisibility(View.INVISIBLE);
                        } else {

                            //썸네일 불러오기
                            FirebaseStorage storage = FirebaseStorage.getInstance("gs://widdy-3d0ed.appspot.com");
                            StorageReference storageRef = storage.getReference();
                            storageRef.child("profile/" + image).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Glide.with(Profile.this).load(uri).fitCenter().listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                            add_profile.setVisibility(View.INVISIBLE);
                                            profile_image.setVisibility(View.VISIBLE);
                                            profile_nickname.setText(nickname);
                                            profile_nickname.setTextColor(Color.parseColor("#FFFFFF"));
                                            profile_page.setVisibility(View.VISIBLE);
                                            profile_progress.setVisibility(View.INVISIBLE);
                                            Log.d("테스트", nickname);
                                            return false;
                                        }
                                    }).into(profile_image);

                                }
                            });
                        }
                    }
                }
            }
        });
    }

    private void profileExist() {
        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        fStore.collection("user").document(currentUser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String nickname = document.getString("nickname");
                        if (nickname == null) {
                            //프로필 만들기 페이지로 이동
                            Intent intent = new Intent(Profile.this, ProfileCreate.class);
                            startActivity(intent);
                        } else {
                            //위디 메인페이지로 이동
                            Intent intent = new Intent(Profile.this, Main.class);
                            startActivity(intent);
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        //액티비티를 종료할 때 애니메이션 없애기
        overridePendingTransition(0, 0);
    }

}