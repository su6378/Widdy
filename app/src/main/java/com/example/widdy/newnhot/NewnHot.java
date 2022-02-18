package com.example.widdy.newnhot;

import android.content.ClipData;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.widdy.R;
import com.example.widdy.main.MovieAdapter;
import com.example.widdy.main.MovieItem;
import com.example.widdy.newnhot.Item.SubItem;
import com.example.widdy.profile.ProfileEtc;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewnHot extends Fragment {

    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private String title_id;

    private ImageView newnhot_profile;

    //탭 레이아웃
    private ConstraintLayout tab_video_openLayout,tab_everyone_likeLayout,tab_top10Layout;
    private TextView tab_video_open_text,tab_everyone_like_text,tab_top10_text;

    //공개예정, 모두가 좋아해요, 톱 10
    private RecyclerView video_open_recyclerview,everyone_like_recyclerview;

    @Override
    public void onResume() {
        super.onResume();
        //데이터 초기화
        getOpen();
        getLike();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_newnhot, container, false);

        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        //프로필 기타 페이지로 이동
        newnhot_profile = view.findViewById(R.id.newnhot_profile);

        newnhot_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ProfileEtc.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
            }
        });

        //탭 레이아웃
        tab_video_openLayout = view.findViewById(R.id.tab_video_openLayout);
        tab_video_open_text = view.findViewById(R.id.tab_video_open_text);

        tab_video_openLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //해당 레이아웃 클릭 시 색깔 변경하고 나머지는 다시 검은색으로 변경
                tab_video_openLayout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F3FFFFFF")));
                tab_video_open_text.setTextColor(Color.parseColor("#FF000000"));
                tab_everyone_likeLayout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF000000")));
                tab_everyone_like_text.setTextColor(Color.parseColor("#ffffffff"));
                tab_top10Layout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF000000")));
                tab_top10_text.setTextColor(Color.parseColor("#ffffffff"));
            }
        });

        tab_everyone_likeLayout = view.findViewById(R.id.tab_everyone_likeLayout);
        tab_everyone_like_text = view.findViewById(R.id.tab_everyone_like_text);

        tab_everyone_likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tab_everyone_likeLayout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F3FFFFFF")));
                tab_everyone_like_text.setTextColor(Color.parseColor("#FF000000"));
                tab_video_openLayout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF000000")));
                tab_video_open_text.setTextColor(Color.parseColor("#ffffffff"));
                tab_top10Layout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF000000")));
                tab_top10_text.setTextColor(Color.parseColor("#ffffffff"));
            }
        });

        tab_top10Layout = view.findViewById(R.id.tab_top10Layout);
        tab_top10_text = view.findViewById(R.id.tab_top10_text);

        tab_top10Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tab_top10Layout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F3FFFFFF")));
                tab_top10_text.setTextColor(Color.parseColor("#FF000000"));
                tab_video_openLayout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF000000")));
                tab_video_open_text.setTextColor(Color.parseColor("#ffffffff"));
                tab_everyone_likeLayout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF000000")));
                tab_everyone_like_text.setTextColor(Color.parseColor("#ffffffff"));
            }
        });

        //공개 예정
        video_open_recyclerview = view.findViewById(R.id.video_open_recyclerview);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        video_open_recyclerview.setLayoutManager(layoutManager);

        //모두가 좋아해요
        everyone_like_recyclerview = view.findViewById(R.id.everyone_like_recyclerview);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        everyone_like_recyclerview.setLayoutManager(layoutManager2);


        //톱 10

        return view;
    }

    //공개 예정 데이터 불러오기
    private void getOpen(){

        //데이터불러올때 터치 막기
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        ArrayList<VideoOpenItem> data = new ArrayList<>();
        VideoOpenAdapter videoOpenAdapter = new VideoOpenAdapter();

        fStore.collection("newnhot").orderBy("order", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String id = document.getId();

                        title_id = document.getId();
                        String month = document.getString("month");
                        String day = document.getString("day");
                        String title = document.getString("title");
                        String open_title = document.getString("open_title");
                        String detail = document.getString("detail");
                        String tag = document.getString("tag");

                        data.add(new VideoOpenItem(id,month,day,title,open_title,detail,tag));


                    }

                    video_open_recyclerview.setAdapter(videoOpenAdapter);
                    video_open_recyclerview.setClickable(false);



                    //아이템 로드
                    videoOpenAdapter.setItems(data);



                    if (!data.isEmpty()){
                        //이미지 로드 완료시 터치 허용
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                }
            }
        });

    }

    //모두가 좋아해요 불러오기
    private void getLike(){

        //데이터불러올때 터치 막기
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        ArrayList<EveryoneLikeItem> data = new ArrayList<>();
        EveryoneLikeAdapter everyoneLikeAdapter = new EveryoneLikeAdapter();

        fStore.collection("everyonelike").orderBy("order", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String id = document.getId();

                        title_id = document.getId();

                        String title = document.getString("title");
                        String subTitle = document.getString("subTitle");
                        String detail = document.getString("detail");
                        String tag = document.getString("tag");

                        data.add(new EveryoneLikeItem(id,title,subTitle,detail,tag));

                        Log.d("테스트", String.valueOf(document.getData()));



                    }

                    everyone_like_recyclerview.setAdapter(everyoneLikeAdapter);
                    everyone_like_recyclerview.setClickable(false);



                    //아이템 로드
                    everyoneLikeAdapter.setItems(data);



                    if (!data.isEmpty()){
                        //이미지 로드 완료시 터치 허용
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                }
            }
        });

    }







}