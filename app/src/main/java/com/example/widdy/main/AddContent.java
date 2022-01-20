package com.example.widdy.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.widdy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class AddContent extends Fragment {

    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private NestedScrollView add_scrollview;
    private RecyclerView add_recyclerView;

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

        add_recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));

        fStore.collection("user").document(currentUser.getEmail()).collection("movie").whereEqualTo("isAdd",true).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String id = document.getId();
                        String title = document.getString("title");

                        title_id = document.getId();

                        data.add(new MovieItem(id,
                                "action", title, "this movie open in 2018.01"));

                    }
                    add_recyclerView.setAdapter(movieAdapter);
                    add_recyclerView.setClickable(false);

                    //아이템 로드
                    movieAdapter.setItems(data);


                }
            }
        });



        return view;
    }
}