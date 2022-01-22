package com.example.widdy.main;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class AddContent extends Fragment {

    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private NestedScrollView add_scrollview;
    private RecyclerView add_recyclerView;
    private ImageView add_profile, add_search,add_filter;

    private MovieAdapter movieAdapter = new MovieAdapter();

    private String title_id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_add_content, container, false);

        //Firebase
        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //찜한 콘텐츠
        add_scrollview = view.findViewById(R.id.add_scrollview);
        add_recyclerView = view.findViewById(R.id.add_recyclerView);

        ArrayList<MovieItem> data = new ArrayList<>();
        MovieAdapter movieAdapter = new MovieAdapter();

        add_recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        fStore.collection("user").document(currentUser.getEmail()).collection("movie").whereEqualTo("isAdd", true).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String id = document.getId();

                        title_id = document.getId();

                        data.add(new MovieItem(id,
                                "action", id, "this movie open in 2018.01"));


                    }
                    add_recyclerView.setAdapter(movieAdapter);
                    add_recyclerView.setClickable(false);

                    //아이템 로드
                    movieAdapter.setItems(data);


                }
            }
        });

        //프로필 기타 페이지로 이동
        add_profile = view.findViewById(R.id.add_profile);

        add_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ProfileEtc.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
            }
        });


        //검색페이지로 이동
        add_search = view.findViewById(R.id.add_search);
        add_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //검색 아이콘 클릭 시
                Intent intent = new Intent(getActivity(), Search.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
            }
        });

        //필터페이지로 이동
        add_filter = view.findViewById(R.id.add_filter);
        add_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),Filter.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
            }
        });


        return view;
    }

}