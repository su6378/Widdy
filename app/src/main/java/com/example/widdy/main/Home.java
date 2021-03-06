package com.example.widdy.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
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


public class Home extends Fragment {

    private ImageView home_image, info_image, info_cancel, home_ic_add,info_ic_add,home_profile,home_search,home_filter;
    private TextView home_nickname, info_title, info_day, info_content, home_image_id;
    private ConstraintLayout home_playBtn, info_bottom_sheet, bottom_layout, home_ic_infoLayout, info_playBtn, home_ic_addLayout,info_saveLayout;
    private ProgressBar bottom_progressbar, main_progressbar;

    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    public String title_id;

    private PlayingAdapter adapter = new PlayingAdapter();
    private MovieAdapter movieAdapter = new MovieAdapter();
    private RecyclerView playing_recyclerView, widdy_movie_recyclerView;
    private NestedScrollView home_scrollView;
    private CardView info_imageLayout;

    private Animation animSlideUp, animSlideDown;

    @Override
    public void onResume() {
        super.onResume();
        //???????????? ????????? ????????????
        playingData();

        //?????? ????????? ????????????
        movieData();

        //?????? ?????????
        initData();
    }


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

        //????????? ?????? ??????
        Window window = getActivity().getWindow();
        window.setNavigationBarColor(Color.parseColor("#282828"));

        //??? ?????????
        home_scrollView = view.findViewById(R.id.home_scrollView);
        home_image = view.findViewById(R.id.home_image);
        home_image_id = view.findViewById(R.id.home_image_id);
        main_progressbar = view.findViewById(R.id.main_progressbar);

        //??????
        home_profile = view.findViewById(R.id.home_profile);
        home_search = view.findViewById(R.id.home_search);
        home_filter = view.findViewById(R.id.home_filter);

        //?????? ?????????
        home_ic_addLayout = view.findViewById(R.id.home_ic_addLayout);
        home_ic_add = view.findViewById(R.id.home_ic_add);

        //????????? ?????? ???????????? ?????????
        home_nickname = view.findViewById(R.id.home_nickname);
        playing_recyclerView = view.findViewById(R.id.playing_recyclerView);

        //?????? ?????????
        widdy_movie_recyclerView = view.findViewById(R.id.widdy_movie_recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        playing_recyclerView.setLayoutManager(layoutManager);

        //?????????????????? ??????
        //??????
        // playing_recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //??????
        widdy_movie_recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));

        //???????????? ????????? ????????????
        playingData();

        //?????? ????????? ????????????
        movieData();

        //?????? ?????????
        initData();

        //????????? ????????? ?????? ???
        home_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ProfileEtc.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
            }
        });

        //?????? ????????? ?????? ???
        home_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Search.class);
                startActivity(intent);
                getActivity().overridePendingTransition( R.anim.slide_left_in, R.anim.slide_left_out );
            }
        });

        //?????? ????????? ?????? ???
        home_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Filter.class);
                startActivity(intent);
                getActivity().overridePendingTransition( R.anim.slide_left_in, R.anim.slide_left_out );
            }
        });

        //??? ???????????? ?????? ???
        home_playBtn = view.findViewById(R.id.home_playBtn);

        home_playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //????????? id??? ????????????????????? ?????????
                title_id = "squid_game";
                isPlay();
            }
        });


        //??? ?????? ????????? ?????? ???
        home_ic_addLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAdd(home_ic_add);
            }
        });

        //????????????
        home_ic_infoLayout = view.findViewById(R.id.home_ic_infoLayout);
        info_image = view.findViewById(R.id.info_image);
        info_bottom_sheet = view.findViewById(R.id.info_bottom_sheet);
        info_title = view.findViewById(R.id.info_title);
        info_day = view.findViewById(R.id.info_day);
        info_content = view.findViewById(R.id.info_content);
        bottom_layout = view.findViewById(R.id.bottom_layout);
        bottom_progressbar = view.findViewById(R.id.bottom_progressbar);
        info_imageLayout = view.findViewById(R.id.info_imageLayout);
        info_playBtn = view.findViewById(R.id.info_playBtn);
        info_saveLayout = view.findViewById(R.id.info_saveLayout);
        info_ic_add = view.findViewById(R.id.info_ic_add);

        //???????????? ???????????????
        animSlideUp = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        animSlideDown = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);


        //??? ?????? ?????? ?????? ??? ???????????? ??????
        home_ic_infoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //????????? id??? ????????????????????? ?????????
                title_id = "squid_game";
                info();
            }
        });

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

                //id ??? ?????????
                title_id = "squid_game";

            }
        });

        //???????????? ????????? ?????? ???
        info_imageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
        info_saveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAdd(info_ic_add);
            }
        });

        return view;
    }

    private void initData() {

        home_scrollView.setVisibility(View.INVISIBLE);
        main_progressbar.setVisibility(View.VISIBLE);

        //????????????????????? ?????? ??????
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        String main_id = "squid_game";
        title_id = "squid_game";

        //??? ?????? ????????? ??????
        //????????? ????????????
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://widdy-3d0ed.appspot.com");
        StorageReference storageRef = storage.getReference();
        storageRef.child("movie/" + main_id + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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
                        home_scrollView.setVisibility(View.VISIBLE);
                        main_progressbar.setVisibility(View.INVISIBLE);
                        //????????? ?????? ????????? ?????? ??????
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        return false;
                    }
                }).into(home_image);

            }
        });


        //????????? ?????? ???????????? ?????????
        fStore.collection("user").document(currentUser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String nickname = document.getString("nickname");
                        home_nickname.setText(nickname + " ?????? ?????? ?????? ?????????");

                    }
                }
            }
        });

        //?????? ????????? ????????? ????????????
        fStore.collection("user").document(currentUser.getEmail()).collection("movie").document("squid_game").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if ( document.getBoolean("isAdd") == null) {
                            //+ ???????????? ??????
                            home_ic_add.setImageResource(R.drawable.ic_add);
                        }else if(document.getBoolean("isAdd") == false ) {
                            //+ ???????????? ??????
                            home_ic_add.setImageResource(R.drawable.ic_add);
                        } else if (document.getBoolean("isAdd") == true) {
                            //?????? ???????????? ??????
                            home_ic_add.setImageResource(R.drawable.ic_check);
                        }
                    } else {
                        //+ ???????????? ??????
                        home_ic_add.setImageResource(R.drawable.ic_add);
                    }
                }
            }
        });



    }

    //?????? ????????? ????????????
    private void movieData() {

        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        ArrayList<MovieItem> data = new ArrayList<>();
        MovieAdapter movieAdapter = new MovieAdapter();

        fStore.collection("video").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String id = document.getId();
                        String title = document.getString("title");

                        title_id = document.getId();

                       /* PlayingItem movie1 = new PlayingItem(id,
                                "action", title, "this movie open in 2018.01");*/
                        data.add(new MovieItem(id,
                                "action", title, "this movie open in 2018.01"));

                        /*//????????????????????????, ???????????? ??????
                        tipBox.setText(document.get("tip").toString().replace("\\n", "\n"));*/


                    }
                    widdy_movie_recyclerView.setAdapter(movieAdapter);

                    //????????? ??????
                    movieAdapter.setItems(data);


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


    //???????????? ????????? ????????? ????????????
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


                        items.add(new PlayingItem(id,
                                "action", title, "this movie open in 2018.01"));

                        /*//????????????????????????, ???????????? ??????
                        tipBox.setText(document.get("tip").toString().replace("\\n", "\n"));*/


                    }
                    playing_recyclerView.setAdapter(adapter);

                    //????????? ??????
                    adapter.setItems(items);

                }
            }
        });
        //????????? ????????? id?????? ???????????? ????????? ??????
        adapter.setItemClickListener(new PlayingAdapter.OnItemClickListner() {
            @Override
            public void OnItemClick(PlayingAdapter.ViewHolder holder, View view, int position) {
                if (view.getId() == R.id.playing_info) {
                    title_id = holder.playing_id.getText().toString();
                    info();

                } else if (view.getId() == R.id.playing_more) {
                    title_id = holder.playing_id.getText().toString();
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
        //info_bottom_sheet.startAnimation(animSlideUp);


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

    //??? ????????? ??????????????? ???????????? ?????? ?????????
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
                                                        playingData();
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
                                                        playingData();
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