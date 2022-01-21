package com.example.widdy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.widdy.main.MovieAdapter;
import com.example.widdy.main.MovieItem;
import com.example.widdy.profile.ProfileEtc;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class Search extends AppCompatActivity {

    private ImageView backBtn, search_profile;
    private EditText search_editText;
    private ConstraintLayout search_filterBtn;
    private TextView search_alert;
    private RecyclerView search_recyclerView;
    private HorizontalScrollView search_scrollView;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore fStore;

    //바텀시트
    private ImageView info_image,info_ic_add;
    private ConstraintLayout info_bottom_sheet,bottom_layout,info_playBtn,info_addLayout;
    private TextView info_title,info_day,info_content;
    private ProgressBar bottom_progressbar;
    private CardView info_imageLayout;

    private MovieAdapter movieAdapter = new MovieAdapter();

    private String title_id;

    //뒤로가기 버튼
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_page);

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

        //프로필 아이콘 클릭 시
        search_profile = findViewById(R.id.search_profile);
        search_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Search.this, ProfileEtc.class);
                startActivity(intent);
            }
        });

        //필터 이동 버튼
        search_filterBtn = findViewById(R.id.search_filterBtn);

        //비디오 검색
        search_editText = findViewById(R.id.search_editText);
        search_alert = findViewById(R.id.search_alert);

        //리사이클러 뷰
        search_scrollView = findViewById(R.id.search_scrollView);
        search_recyclerView = findViewById(R.id.search_recyclerView);

        search_recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        search_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String data = search_editText.getText().toString();
                search();
                search_alert.setText("\"" + data + "\" 키워드가 제목에 포함된 TV 프로그램 & 영화");
                search_alert.setVisibility(View.VISIBLE);
                search_scrollView.setVisibility(View.VISIBLE);
                if (search_editText.getText().toString().isEmpty()) {
                    search_alert.setVisibility(View.INVISIBLE);
                    search_scrollView.setVisibility(View.INVISIBLE);
                    search_filterBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

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
        info_addLayout = findViewById(R.id.info_saveLayout);
        info_ic_add = findViewById(R.id.info_ic_add);

    }

    //검색 메소드
    private void search() {
        //Firebase
        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        search_filterBtn.setVisibility(View.INVISIBLE);

        //검색 바에서 받아오는 값값
        String data = search_editText.getText().toString();

        ArrayList<MovieItem> movie = new ArrayList<>();
        MovieAdapter movieAdapter = new MovieAdapter();

        fStore.collection("video").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String genre = document.getString("genre");
                        String id = document.getId();
                        String title = document.getString("title");
                        String content = document.getString("content");

                        if (genre.contains(data) || title.contains(data)) {
                            movie.add(new MovieItem(id, genre, title, content));
                        } else {
                            // Log.d("테스트","실패");
                        }
                    }
                    search_recyclerView.setAdapter(movieAdapter);
                    //아이템 로드
                    movieAdapter.setItems(movie);
                }
            }
        });
        /*//포스터 클릭시 id값을 변경하고 메소드 실행
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
        });*/
    }

    //정보 버튼 클릭시
    private void info() {
        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        info_bottom_sheet.setVisibility(View.VISIBLE);
        bottom_progressbar.setVisibility(View.VISIBLE);
        //info_bottom_sheet.startAnimation(animSlideUp);


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
                                Glide.with(getActivity()).load(uri).centerCrop().listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        Log.d("테스트", "실패");
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        bottom_layout.setVisibility(View.VISIBLE);
                                        bottom_progressbar.setVisibility(View.INVISIBLE);
                                        //info_bottom_sheet.clearAnimation();
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

}