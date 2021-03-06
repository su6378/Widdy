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
import com.example.widdy.main.Main;
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

    //???????????? ??????
    @Override
    public void onBackPressed() {

        //??????????????? onBackPressedListener??????
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragmentList) {
            if (fragment instanceof onBackPressedListener) {
                ((onBackPressedListener) fragment).onBackPressed();
                return;
            }
        }

        //??? ??? ????????? ?????? ??????
        if (System.currentTimeMillis() - lastTimeBackPressed < 1500) {
            finish();
            return;
        }
        lastTimeBackPressed = System.currentTimeMillis();
        Toast.makeText(this, "'??????' ????????? ??? ??? ??? ????????? ???????????????.", Toast.LENGTH_SHORT).show();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);

        //????????? ?????? ??????
        getWindow().setNavigationBarColor(Color.parseColor("#282828"));


        //??????????????????
        profile_progress = findViewById(R.id.profile_progress);
        profile_page = findViewById(R.id.profile_page);

        //?????????
        add_profile = findViewById(R.id.add_profile);
        profile_image = findViewById(R.id.profile_image);
        profile_nickname = findViewById(R.id.profile_nickname);

        //????????? ?????????
        initData();


        //????????? ?????? or ?????? or ?????? ????????? ??????
        nextBtn = findViewById(R.id.nextBtn);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                profileExist();
            }
        });

        //????????? ??????
        profile_update = findViewById(R.id.profile_update);
        profile_text = findViewById(R.id.profile_text);
        profile_logo = findViewById(R.id.profile_logo);

        profile_update.setOnClickListener(new View.OnClickListener() {
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

                                    //???????????? ?????? ???????????? ??????
                                    Intent intent = new Intent(Profile.this, ProfileManage.class);
                                    intent.putExtra("nickname", nickname);
                                    overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
                                    startActivity(intent);


                                }
                            }
                        }
                    }
                });
            }
        });


    }
    //????????? ?????????
    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {

        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //progressbar ?????????
        profile_page.setVisibility(View.INVISIBLE);


        fStore.collection("user").document(currentUser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String nickname = document.getString("nickname");
                        String image = document.getString("image");
                        if (document.getString("nickname") == null) {
                            profile_nickname.setText("????????? ??????");
                            profile_page.setVisibility(View.VISIBLE);
                            profile_progress.setVisibility(View.INVISIBLE);
                            add_profile.setVisibility(View.VISIBLE);
                            profile_image.setVisibility(View.INVISIBLE);
                        } else {
                            //????????? ?????? ??????
                            profile_nickname.setText(nickname);
                            profile_nickname.setTextColor(Color.parseColor("#FFFFFF"));

                            //????????? ????????????
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
                                            profile_page.setVisibility(View.VISIBLE);
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
                            //????????? ????????? ???????????? ??????
                            Intent intent = new Intent(Profile.this, ProfileCreate.class);
                            startActivity(intent);
                        } else {
                            //?????? ?????????????????? ??????
                            Intent intent = new Intent(Profile.this, Main.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        //??????????????? ????????? ??? ??????????????? ?????????
        overridePendingTransition(0, 0);
    }

}