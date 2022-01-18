package com.example.widdy.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.widdy.R;
import com.example.widdy.profile.Profile;
import com.example.widdy.profile.ProfileUpdate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Home extends Fragment {

    private ImageView home_image, info_image,info_cancel;
    private TextView home_nickname, info_title, info_day,info_content;
    private ConstraintLayout home_playBtn, info_bottom_sheet, bottom_layout,home_ic_infoLayout;
    private ProgressBar bottom_progressbar, main_progressbar;

    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    public String title_id;

    private PlayingAdapter adapter = new PlayingAdapter();
    private RecyclerView playing_recyclerView;
    private ScrollView home_scrollView;

    private Animation animSlideUp,animSlideDown;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_home, container, false);

        //Firebase
        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //하단바 색깔 적용
        Window window = getActivity().getWindow();
        window.setNavigationBarColor(Color.parseColor("#282828"));

        //홈 이미지
        home_scrollView = view.findViewById(R.id.home_scrollView);
        home_image = view.findViewById(R.id.home_image);
        main_progressbar = view.findViewById(R.id.main_progressbar);

        //닉네임 님이 시청중인 콘텐츠
        home_nickname = view.findViewById(R.id.home_nickname);
        playing_recyclerView = view.findViewById(R.id.playing_recyclerView);
        playing_recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //리사이클러뷰 방향
        //세로
        // playing_recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //가로
        playing_recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));

        //시청중인 콘텐츠 불러오기
        playingData();

        //화면 초기화
        initData();


        //재생버튼 클릭 시
        home_playBtn = view.findViewById(R.id.home_playBtn);

        home_playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity(), R.style.MyAlertDialogStyle);

                builder.setTitle("재생")
                        .setMessage("\n영상을 시청하시겠습니까?")

                        .setCancelable(false)

                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {

                                fStore = FirebaseFirestore.getInstance();
                                mAuth = FirebaseAuth.getInstance();
                                currentUser = mAuth.getCurrentUser();

                                DocumentReference documentReference = fStore.collection("user").document(currentUser.getEmail()).collection("movie")
                                        .document("squid_game");

                                Map<String, Object> user = new HashMap<>();
                                user.put("isPlay", true);

                                documentReference.set(user, SetOptions.merge());


                            }

                        }).setNegativeButton("취소", null).show();

            }
        });

        //바텀시트
        home_ic_infoLayout = view.findViewById(R.id.home_ic_infoLayout);
        info_image = view.findViewById(R.id.info_image);
        info_bottom_sheet = view.findViewById(R.id.info_bottom_sheet);
        info_title = view.findViewById(R.id.info_title);
        info_day = view.findViewById(R.id.info_day);
        info_content = view.findViewById(R.id.info_content);
        bottom_layout = view.findViewById(R.id.bottom_layout);
        bottom_progressbar = view.findViewById(R.id.bottom_progressbar);

        //바텀시트 애니메이션
        animSlideUp = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        animSlideDown = AnimationUtils.loadAnimation(getActivity(),R.anim.slide_down);


        //정보 버튼 클릭 시 바텀시트 팝업
        home_ic_infoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String id = "squid_game";
                info_bottom_sheet.setElevation(10);
                info_bottom_sheet.setVisibility(View.VISIBLE);
                bottom_layout.setVisibility(View.INVISIBLE);
                bottom_progressbar.setVisibility(View.VISIBLE);
                info_bottom_sheet.startAnimation(animSlideUp);

                //제목 불러오기
                fStore.collection("video").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                                                return false;
                                            }
                                        }).into(info_image);

                                    }
                                });


                            }
                        }
                    }
                });
            }
        });

        //정보창 닫기 버튼
        info_cancel = view.findViewById(R.id.info_cancel);
        info_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                info_bottom_sheet.setVisibility(View.INVISIBLE);
                bottom_layout.setVisibility(View.VISIBLE);
                bottom_progressbar.setVisibility(View.INVISIBLE);

            }
        });

        return view;
    }

    private void initData() {

        home_scrollView.setVisibility(View.INVISIBLE);
        main_progressbar.setVisibility(View.VISIBLE);

        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        String main_id = "squid_game";

        //홈 메인 이미지 적용
        //이미지 불러오기
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://widdy-3d0ed.appspot.com");
        StorageReference storageRef = storage.getReference();
        storageRef.child("movie/" + main_id + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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
                        home_scrollView.setVisibility(View.VISIBLE);
                        main_progressbar.setVisibility(View.INVISIBLE);
                        return false;
                    }
                }).into(home_image);

            }
        });


        //닉네임 님이 시청중인 콘텐츠
        fStore.collection("user").document(currentUser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String nickname = document.getString("nickname");
                        home_nickname.setText(nickname + " 님이 시청 중인 콘텐츠");

                    }
                }
            }
        });

    }

    //시청중인 콘텐츠 데이터 불러오기
    private void playingData() {

        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        ArrayList<PlayingItem> items = new ArrayList<>();
        PlayingAdapter adapter = new PlayingAdapter();


        fStore.collection("user").document(currentUser.getEmail()).collection("movie").whereEqualTo("isPlay", true)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {


                        String id = document.getId();
                        String title = document.getString("title");

                        title_id = document.getId();

                        PlayingItem movie1 = new PlayingItem(id,
                                "action", title, "this movie open in 2018.01");

                        items.add(new PlayingItem(id,
                                "action", title, "this movie open in 2018.01"));

                        /*//활동내용불러오기, 개행문자 변환
                        tipBox.setText(document.get("tip").toString().replace("\\n", "\n"));*/


                    }
                    playing_recyclerView.setAdapter(adapter);

                    //아이템 로드
                    adapter.setItems(items);


                }
            }
        });

        adapter.setItemClickListener(new PlayingAdapter.OnItemClickListner() {
            @Override
            public void OnItemClick(PlayingAdapter.ViewHolder holder, View view, int position) {
                Log.d("테스트", holder.playing_id.getText().toString());
            }
        });


    }

}