package com.example.widdy.main;

import android.content.DialogInterface;
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
import android.view.WindowManager;
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


public class AddContent extends Fragment {

    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private NestedScrollView add_scrollview;
    private RecyclerView add_recyclerView;
    private ImageView add_profile, add_search,add_filter;

    private MovieAdapter movieAdapter = new MovieAdapter();

    private String title_id;

    //????????????
    private ImageView info_image,info_ic_add,info_cancel;
    private ConstraintLayout info_bottom_sheet,bottom_layout,info_playBtn,info_addLayout;
    private TextView info_title,info_day,info_content;
    private ProgressBar bottom_progressbar;
    private CardView info_imageLayout;


    @Override
    public void onResume() {
        super.onResume();
        //????????? ?????????
        initData();
    }

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

        //????????????
        info_image = view.findViewById(R.id.info_image);
        info_bottom_sheet = view.findViewById(R.id.info_bottom_sheet);
        info_title = view.findViewById(R.id.info_title);
        info_day = view.findViewById(R.id.info_day);
        info_content = view.findViewById(R.id.info_content);
        bottom_layout = view.findViewById(R.id.bottom_layout);
        bottom_progressbar = view.findViewById(R.id.bottom_progressbar);
        info_imageLayout = view.findViewById(R.id.info_imageLayout);
        info_playBtn = view.findViewById(R.id.info_playBtn);
        info_addLayout = view.findViewById(R.id.info_addLayout);
        info_ic_add = view.findViewById(R.id.info_ic_add);

        //????????? ?????? ??????
        info_cancel = view.findViewById(R.id.info_cancel);
        info_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                info_bottom_sheet.setVisibility(View.INVISIBLE);
                bottom_layout.setVisibility(View.INVISIBLE);
                bottom_progressbar.setVisibility(View.INVISIBLE);

                //?????? ?????? ??????
                info_bottom_sheet.setClickable(false);

            }
        });

        //???????????? ?????? ?????? ?????????
        info_playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPlay();
            }
        });

        //???????????? ????????? ?????? ?????????
        info_addLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAdd(info_ic_add);
            }
        });

        //?????? ?????????
        add_scrollview = view.findViewById(R.id.add_scrollview);
        add_recyclerView = view.findViewById(R.id.add_recyclerView);


        //????????? ?????? ???????????? ??????
        add_profile = view.findViewById(R.id.add_profile);

        add_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ProfileEtc.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
            }
        });


        //?????????????????? ??????
        add_search = view.findViewById(R.id.add_search);
        add_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //?????? ????????? ?????? ???
                Intent intent = new Intent(getActivity(), Search.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
            }
        });

        //?????????????????? ??????
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
    private void initData(){

        //????????????????????? ?????? ??????
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

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

                    //????????? ??????
                    movieAdapter.setItems(data);

                    if (!data.isEmpty()){
                        //????????? ?????? ????????? ?????? ??????
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                }
            }
        });

        //????????? ????????? id?????? ???????????? ????????? ??????
        movieAdapter.setItemClickListener(new MovieAdapter.OnItemClickListner() {
            @Override
            public void OnItemClick(MovieAdapter.ViewHolder holder, View view, int position) {

                //isPlay?????? ????????? ???????????? ????????? info ??? ????????? ?????????
                if (view.getId() == R.id.movie_layout) {
                    title_id = holder.movie_id.getText().toString();
                    fStore.collection("user").document(currentUser.getEmail()).collection("movie").document(title_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                //?????? ???????????? ?????? ?????? ?????????
                                if (document.exists()) {
                                    info();
                                    //?????? ?????? ?????? ?????????
                                    info_bottom_sheet.setClickable(true);
                                } else {
                                    //user ???????????? isPlay,isAdd??? ??????
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
    //?????? ?????? ?????????
    private void info() {
        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        info_bottom_sheet.setVisibility(View.VISIBLE);
        bottom_progressbar.setVisibility(View.VISIBLE);


        //?????? ????????????
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

                        //????????? ????????????
                        FirebaseStorage storage = FirebaseStorage.getInstance("gs://widdy-3d0ed.appspot.com");
                        StorageReference storageRef = storage.getReference();
                        storageRef.child("movie/" + id + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Glide.with(getActivity()).load(uri).centerCrop().listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        Log.d("?????????", "??????");
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

        //?????? ????????? ????????? ????????????
        fStore.collection("user").document(currentUser.getEmail()).collection("movie").document(title_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if ( document.getBoolean("isAdd") == null) {
                            //+ ???????????? ??????
                            info_ic_add.setImageResource(R.drawable.ic_add);
                        }else if(document.getBoolean("isAdd") == false ) {
                            //+ ???????????? ??????
                            info_ic_add.setImageResource(R.drawable.ic_add);
                        } else if (document.getBoolean("isAdd") == true) {
                            //?????? ???????????? ??????
                            info_ic_add.setImageResource(R.drawable.ic_check);
                        }
                    } else {
                        //+ ???????????? ??????
                        info_ic_add.setImageResource(R.drawable.ic_add);
                    }
                }
            }
        });
    }

    //??????????????? ???????????? ?????? ?????????
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
                            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity(), R.style.MyAlertDialogStyle);

                            builder.setTitle("??????")
                                    .setMessage("\n????????? ?????????????????????????")

                                    .setCancelable(false)

                                    .setPositiveButton("??????", new DialogInterface.OnClickListener() {

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

                                    }).setNegativeButton("??????", null).show();
                        } else if (document.getBoolean("isPlay") == true) {
                            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity(), R.style.MyAlertDialogStyle);

                            builder.setTitle("????????????")
                                    .setMessage("\n????????? ????????? ?????????????????????????")

                                    .setCancelable(false)

                                    .setPositiveButton("??????", new DialogInterface.OnClickListener() {

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
                                    }).setNegativeButton("??????", null).show();
                        }
                    }
                }
            }
        });
    }

    //???????????? ???????????? ?????? ?????????
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
                        //?????? ?????? ????????? ??? ??????
                        if(document.getBoolean("isAdd") == null){
                            DocumentReference documentReference = fStore.collection("user").document(currentUser.getEmail()).collection("movie")
                                    .document(title_id);

                            Map<String, Object> user = new HashMap<>();
                            user.put("isAdd", true);

                            documentReference.set(user, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //+ ???????????? ??????
                                        imageview.setImageResource(R.drawable.ic_check);
                                        initData();
                                    }
                                }
                            });
                        }
                        //????????? ?????? ????????? ??????
                        else if (document.getBoolean("isAdd") == true) {
                            DocumentReference documentReference = fStore.collection("user").document(currentUser.getEmail()).collection("movie")
                                    .document(title_id);

                            Map<String, Object> user = new HashMap<>();
                            user.put("isAdd", false);

                            documentReference.set(user, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //+ ???????????? ??????
                                        imageview.setImageResource(R.drawable.ic_add);
                                        initData();
                                    }
                                }
                            });
                        } else if((document.getBoolean("isAdd") == false)) {
                            //?????? ????????? ????????? ????????? ?????? ????????? ??????
                            DocumentReference documentReference = fStore.collection("user").document(currentUser.getEmail()).collection("movie")
                                    .document(title_id);

                            Map<String, Object> user = new HashMap<>();
                            user.put("isAdd", true);

                            documentReference.set(user, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //?????? ???????????? ??????
                                        imageview.setImageResource(R.drawable.ic_check);
                                        initData();
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