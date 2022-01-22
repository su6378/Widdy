package com.example.widdy.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.widdy.R;
import com.example.widdy.profile.ProfileEtc;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Filter extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore fStore;

    private ImageView backBtn,filter_search,filter_profile;
    private NestedScrollView filter_scrollview;
    private RecyclerView filter_recyclerView;

    private MovieAdapter movieAdapter = new MovieAdapter();
    private String title_id;

    //바텀시트
    private ImageView info_image,info_ic_add,info_cancel;
    private ConstraintLayout info_bottom_sheet,bottom_layout,info_playBtn,info_addLayout;
    private TextView info_title,info_day,info_content;
    private ProgressBar bottom_progressbar;
    private CardView info_imageLayout;

    //뒤로가기 버튼
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //데이터 초기화
        initData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_page);

        //하단바 색깔 적용
        getWindow().setNavigationBarColor(Color.parseColor("#282828"));

        //Firebase
        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //뒤로가기
        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
            }
        });

        //검색 아이콘 클릭 시
        filter_search = findViewById(R.id.filter_search);
        filter_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Filter.this,Search.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
            }
        });

        //프로필 아이콘 클릭 시
        filter_profile = findViewById(R.id.filter_profile);
        filter_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Filter.this, ProfileEtc.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
            }
        });


        //위디 콘텐츠
        filter_scrollview = findViewById(R.id.filter_scrollview);
        filter_recyclerView = findViewById(R.id.filter_recyclerView);

        //바텀시트
        info_image = findViewById(R.id.info_image);
        info_bottom_sheet = findViewById(R.id.info_bottom_sheet);
        info_title = findViewById(R.id.info_title);
        info_day = findViewById(R.id.info_day);
        info_content = findViewById(R.id.info_content);
        bottom_layout = findViewById(R.id.bottom_layout);
        bottom_progressbar = findViewById(R.id.bottom_progressbar);
        info_imageLayout = findViewById(R.id.info_imageLayout);
        info_playBtn = findViewById(R.id.info_playBtn);
        info_addLayout = findViewById(R.id.info_addLayout);
        info_ic_add = findViewById(R.id.info_ic_add);

        //정보창 닫기 버튼
        info_cancel = findViewById(R.id.info_cancel);
        info_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                info_bottom_sheet.setVisibility(View.INVISIBLE);
                bottom_layout.setVisibility(View.INVISIBLE);
                bottom_progressbar.setVisibility(View.INVISIBLE);

                //뒤에 터치 해제
                info_bottom_sheet.setClickable(false);

            }
        });

        //바텀시트 재생 버튼 클릭시
        info_playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPlay();
            }
        });

        //바텀시트 찜하기 버튼 클릭시
        info_addLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAdd(info_ic_add);
            }
        });


    }

    private void initData(){

        ArrayList<MovieItem> data = new ArrayList<>();
        MovieAdapter movieAdapter = new MovieAdapter();

        filter_recyclerView.setLayoutManager(new GridLayoutManager(Filter.this, 3));

        fStore.collection("video").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String id = document.getId();

                        title_id = document.getId();

                        data.add(new MovieItem(id,
                                "action", id, "this movie open in 2018.01"));


                    }
                    filter_recyclerView.setAdapter(movieAdapter);
                    filter_recyclerView.setClickable(false);

                    //아이템 로드
                    movieAdapter.setItems(data);


                }
            }
        });

        //포스터 클릭시 id값을 변경하고 메소드 실행
        movieAdapter.setItemClickListener(new MovieAdapter.OnItemClickListner() {
            @Override
            public void OnItemClick(MovieAdapter.ViewHolder holder, View view, int position) {

                //isPlay값이 없으면 추가하고 있으면 info 창 그대로 띄우기
                if (view.getId() == R.id.movie_layout) {
                    title_id = holder.movie_id.getText().toString();
                    fStore.collection("user").document(currentUser.getEmail()).collection("movie").document(title_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                //값이 존재하면 그냥 창만 띄우기
                                if (document.exists()) {
                                    info();
                                    //뒤에 화면 터치 막기기
                                    info_bottom_sheet.setClickable(true);
                                } else {
                                    //user 콜렉션에 isPlay,isAdd값 추가
                                    DocumentReference documentReference = fStore.collection("user").document(currentUser.getEmail()).collection("movie")
                                            .document(title_id);

                                    Map<String, Object> user = new HashMap<>();
                                    user.put("isPlay", false);


                                    documentReference.set(user, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                info();
                                                info_bottom_sheet.setClickable(true);
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
            }
        });

    }
    //정보 버튼 클릭시
    private void info() {
        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        info_bottom_sheet.setVisibility(View.VISIBLE);
        bottom_progressbar.setVisibility(View.VISIBLE);


        //제목 불러오기
        fStore.collection("video").document(title_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String id = document.getId();
                        String title = document.getString("title");
                        String year = document.getString("year");
                        String content = document.getString("content");
                        info_title.setText(title);
                        info_day.setText(year);
                        info_content.setText(content);

                        //이미지 불러오기
                        FirebaseStorage storage = FirebaseStorage.getInstance("gs://widdy-3d0ed.appspot.com");
                        StorageReference storageRef = storage.getReference();
                        storageRef.child("movie/" + id + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Glide.with(Filter.this).load(uri).centerCrop().listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        Log.d("테스트", "실패");
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        bottom_layout.setVisibility(View.VISIBLE);
                                        bottom_progressbar.setVisibility(View.INVISIBLE);
                                        return false;
                                    }
                                }).into(info_image);

                            }
                        });


                    }
                }
            }
        });

        //찜한 콘텐츠 아이콘 불러오기
        fStore.collection("user").document(currentUser.getEmail()).collection("movie").document(title_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if ( document.getBoolean("isAdd") == null) {
                            //+ 이미지로 변경
                            info_ic_add.setImageResource(R.drawable.ic_add);
                        }else if(document.getBoolean("isAdd") == false ) {
                            //+ 이미지로 변경
                            info_ic_add.setImageResource(R.drawable.ic_add);
                        } else if (document.getBoolean("isAdd") == true) {
                            //체크 이미지로 변경
                            info_ic_add.setImageResource(R.drawable.ic_check);
                        }
                    } else {
                        //+ 이미지로 변경
                        info_ic_add.setImageResource(R.drawable.ic_add);
                    }
                }
            }
        });
    }

    //재생했는지 안했는지 판단 메소드
    private void isPlay() {
        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        fStore.collection("user").document(currentUser.getEmail()).collection("movie").document(title_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if (document.getBoolean("isPlay") == false || document.getBoolean("isPlay") == null) {
                            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(Filter.this, R.style.MyAlertDialogStyle);

                            builder.setTitle("재생")
                                    .setMessage("\n영상을 시청하시겠습니까?")

                                    .setCancelable(false)

                                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int id) {

                                            fStore = FirebaseFirestore.getInstance();
                                            mAuth = FirebaseAuth.getInstance();
                                            currentUser = mAuth.getCurrentUser();

                                            DocumentReference documentReference = fStore.collection("user").document(currentUser.getEmail()).collection("movie")
                                                    .document(title_id);

                                            Map<String, Object> user = new HashMap<>();
                                            user.put("isPlay", true);

                                            documentReference.set(user, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                    }
                                                }
                                            });
                                        }

                                    }).setNegativeButton("취소", null).show();
                        } else if (document.getBoolean("isPlay") == true) {
                            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(Filter.this, R.style.MyAlertDialogStyle);

                            builder.setTitle("재생완료")
                                    .setMessage("\n영상을 시청을 취소하시겠습니까?")

                                    .setCancelable(false)

                                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int id) {

                                            fStore = FirebaseFirestore.getInstance();
                                            mAuth = FirebaseAuth.getInstance();
                                            currentUser = mAuth.getCurrentUser();

                                            DocumentReference documentReference = fStore.collection("user").document(currentUser.getEmail()).collection("movie")
                                                    .document(title_id);

                                            Map<String, Object> user = new HashMap<>();
                                            user.put("isPlay", false);

                                            documentReference.set(user, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                    }
                                                }
                                            });
                                        }
                                    }).setNegativeButton("취소", null).show();
                        }
                    }
                }
            }
        });
    }

    //찜했는지 안했는지 판단 메소드
    private void isAdd(ImageView imageview) {
        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        fStore.collection("user").document(currentUser.getEmail()).collection("movie").document(title_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //찜한 적이 없으면 값 추가
                        if(document.getBoolean("isAdd") == null){
                            DocumentReference documentReference = fStore.collection("user").document(currentUser.getEmail()).collection("movie")
                                    .document(title_id);

                            Map<String, Object> user = new HashMap<>();
                            user.put("isAdd", true);

                            documentReference.set(user, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //+ 이미지로 변경
                                        imageview.setImageResource(R.drawable.ic_check);
                                    }
                                }
                            });
                        }
                        //찜했을 때는 취소로 변경
                        else if (document.getBoolean("isAdd") == true) {
                            DocumentReference documentReference = fStore.collection("user").document(currentUser.getEmail()).collection("movie")
                                    .document(title_id);

                            Map<String, Object> user = new HashMap<>();
                            user.put("isAdd", false);

                            documentReference.set(user, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //+ 이미지로 변경
                                        imageview.setImageResource(R.drawable.ic_add);
                                    }
                                }
                            });
                        } else if((document.getBoolean("isAdd") == false)) {
                            //찜한 상태가 아니면 반대로 찜한 목록에 추가
                            DocumentReference documentReference = fStore.collection("user").document(currentUser.getEmail()).collection("movie")
                                    .document(title_id);

                            Map<String, Object> user = new HashMap<>();
                            user.put("isAdd", true);

                            documentReference.set(user, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //체크 이미지로 변경
                                        imageview.setImageResource(R.drawable.ic_check);
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }
}