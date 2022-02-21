package com.example.widdy.newnhot;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.widdy.R;
import com.example.widdy.profile.ProfileEtc;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class NewnHot extends Fragment {

    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private String title_id;

    private ImageView newnhot_profile;

    //탭 레이아웃
    private NestedScrollView newnhot_scrollView;
    private ConstraintLayout tab_video_openLayout, tab_everyone_likeLayout, tab_top10Layout, video_openLayout, video_everyone_likeLayout, top_10Layout;
    private TextView tab_video_open_text, tab_everyone_like_text, tab_top10_text;
    private boolean isScroll = false;
    private GestureDetector detector;

    //공개예정, 모두가 좋아해요, 톱 10
    private RecyclerView video_open_recyclerview, everyone_like_recyclerview, top_10_recyclerview;

    @Override
    public void onResume() {
        super.onResume();
        //데이터 초기화
        getOpen();
        getLike();
        getTop10();

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
        newnhot_scrollView = view.findViewById(R.id.newnhot_scrollView);

        video_openLayout = view.findViewById(R.id.video_openLayout);
        video_everyone_likeLayout = view.findViewById(R.id.video_everyone_likeLayout);
        top_10Layout = view.findViewById(R.id.top_10Layout);

        tab_video_openLayout = view.findViewById(R.id.tab_video_openLayout);
        tab_video_open_text = view.findViewById(R.id.tab_video_open_text);

        tab_video_openLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //스크롤 제어
                isScroll = false;
                if (!isScroll) {
                    //해당 레이아웃 클릭 시 색깔 변경하고 나머지는 다시 검은색으로 변경
                    tab_video_openLayout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F3FFFFFF")));
                    tab_video_open_text.setTextColor(Color.parseColor("#FF000000"));
                    tab_everyone_likeLayout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF000000")));
                    tab_everyone_like_text.setTextColor(Color.parseColor("#ffffffff"));
                    tab_top10Layout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF000000")));
                    tab_top10_text.setTextColor(Color.parseColor("#ffffffff"));

                    //해당 레이아웃의 top쪽으로 scroll 이동
                    newnhot_scrollView.smoothScrollTo(0, video_openLayout.getTop());
                }
            }
        });

        tab_everyone_likeLayout = view.findViewById(R.id.tab_everyone_likeLayout);
        tab_everyone_like_text = view.findViewById(R.id.tab_everyone_like_text);

        tab_everyone_likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //스크롤 제어
                isScroll = false;
                if (!isScroll) {
                    tab_everyone_likeLayout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F3FFFFFF")));
                    tab_everyone_like_text.setTextColor(Color.parseColor("#FF000000"));
                    tab_video_openLayout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF000000")));
                    tab_video_open_text.setTextColor(Color.parseColor("#ffffffff"));
                    tab_top10Layout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF000000")));
                    tab_top10_text.setTextColor(Color.parseColor("#ffffffff"));
                    newnhot_scrollView.smoothScrollTo(0, video_everyone_likeLayout.getTop());
                }
            }
        });

        tab_top10Layout = view.findViewById(R.id.tab_top10Layout);
        tab_top10_text = view.findViewById(R.id.tab_top10_text);


        tab_top10Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //스크롤 제어
                isScroll = false;
                if(!isScroll){
                    tab_top10Layout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F3FFFFFF")));
                    tab_top10_text.setTextColor(Color.parseColor("#FF000000"));
                    tab_video_openLayout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF000000")));
                    tab_video_open_text.setTextColor(Color.parseColor("#ffffffff"));
                    tab_everyone_likeLayout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF000000")));
                    tab_everyone_like_text.setTextColor(Color.parseColor("#ffffffff"));
                    newnhot_scrollView.smoothScrollTo(0, top_10Layout.getTop());
                }
            }
        });

        //스크롤시 스크롤의 y위치 값을 받아 각 탭의 레이아웃 상단바를 지날때 탭도 변경
        if (newnhot_scrollView != null) {
            newnhot_scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    //스크롤할시 탭 전환
                    if (isScroll == true) {
                        if (scrollY <= video_openLayout.getBottom()) {
                            tab_video_openLayout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F3FFFFFF")));
                            tab_video_open_text.setTextColor(Color.parseColor("#FF000000"));
                            tab_everyone_likeLayout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF000000")));
                            tab_everyone_like_text.setTextColor(Color.parseColor("#ffffffff"));
                            tab_top10Layout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF000000")));
                            tab_top10_text.setTextColor(Color.parseColor("#ffffffff"));
                        } else if (scrollY > video_openLayout.getBottom()  && scrollY <= video_everyone_likeLayout.getBottom() ) {
                            tab_everyone_likeLayout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F3FFFFFF")));
                            tab_everyone_like_text.setTextColor(Color.parseColor("#FF000000"));
                            tab_video_openLayout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF000000")));
                            tab_video_open_text.setTextColor(Color.parseColor("#ffffffff"));
                            tab_top10Layout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF000000")));
                            tab_top10_text.setTextColor(Color.parseColor("#ffffffff"));
                        } else if (scrollY > video_everyone_likeLayout.getBottom() && scrollY <= top_10Layout.getBottom()) {
                            tab_top10Layout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F3FFFFFF")));
                            tab_top10_text.setTextColor(Color.parseColor("#FF000000"));
                            tab_video_openLayout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF000000")));
                            tab_video_open_text.setTextColor(Color.parseColor("#ffffffff"));
                            tab_everyone_likeLayout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF000000")));
                            tab_everyone_like_text.setTextColor(Color.parseColor("#ffffffff"));
                        }
                    }

                }
            });
        }

        //공개 예정
        video_open_recyclerview = view.findViewById(R.id.video_open_recyclerview);

        //리사이클러뷰 스크롤시 감지해서 isScroll 값 변경
        video_open_recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    isScroll = false;
                } else {
                    isScroll = true;
                }
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        video_open_recyclerview.setLayoutManager(layoutManager);

        //모두가 좋아해요
        everyone_like_recyclerview = view.findViewById(R.id.everyone_like_recyclerview);
        everyone_like_recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    isScroll = false;
                } else {
                    isScroll = true;
                }
            }
        });

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        everyone_like_recyclerview.setLayoutManager(layoutManager2);


        //톱 10
        top_10_recyclerview = view.findViewById(R.id.top_10_recyclerview);
        top_10_recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    isScroll = false;
                } else {
                    isScroll = true;
                }
            }
        });

        LinearLayoutManager layoutManager3 = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        top_10_recyclerview.setLayoutManager(layoutManager3);


        return view;
    }

    //공개 예정 데이터 불러오기
    private void getOpen() {

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

                        data.add(new VideoOpenItem(id, month, day, title, open_title, detail, tag));


                    }

                    video_open_recyclerview.setAdapter(videoOpenAdapter);
                    video_open_recyclerview.setClickable(false);


                    //아이템 로드
                    videoOpenAdapter.setItems(data);


                    if (!data.isEmpty()) {
                        //이미지 로드 완료시 터치 허용
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                }
            }
        });

    }

    //모두가 좋아해요 불러오기
    private void getLike() {

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

                        data.add(new EveryoneLikeItem(id, title, subTitle, detail, tag));


                    }

                    everyone_like_recyclerview.setAdapter(everyoneLikeAdapter);
                    everyone_like_recyclerview.setClickable(false);


                    //아이템 로드
                    everyoneLikeAdapter.setItems(data);


                    if (!data.isEmpty()) {
                        //이미지 로드 완료시 터치 허용
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                }
            }
        });

    }

    //톱 10 불러오기
    private void getTop10() {

        //데이터불러올때 터치 막기
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        ArrayList<Top10Item> data = new ArrayList<>();
        Top10Adapter top10Adapter = new Top10Adapter();

        fStore.collection("top10").orderBy("order", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                        String ranking = String.valueOf(document.getLong("order"));

                        data.add(new Top10Item(id, title, subTitle, detail, tag, ranking));


                    }

                    top_10_recyclerview.setAdapter(top10Adapter);
                    top_10_recyclerview.setClickable(false);


                    //아이템 로드
                    top10Adapter.setItems(data);


                    if (!data.isEmpty()) {
                        //이미지 로드 완료시 터치 허용
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                }
            }
        });

    }

    public static void scrollToView(View view, final NestedScrollView scrollView, int count) {
        if (view != null && view != scrollView) {
            count += view.getTop();
            scrollToView((View) view.getParent(), scrollView, count);
        } else if (scrollView != null) {
            final int finalCount = count;
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    scrollView.smoothScrollTo(0, finalCount);
                }
            }, 200);
        }
    }


}