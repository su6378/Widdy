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
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
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
import com.google.android.material.checkbox.MaterialCheckBox;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Filter extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore fStore;

    private ImageView backBtn, filter_search, filter_profile;
    private NestedScrollView filter_scrollview;
    private RecyclerView filter_recyclerView;

    private MovieAdapter movieAdapter = new MovieAdapter();
    private String title_id;

    //????????????
    private ImageView info_image, info_ic_add, info_cancel;
    private ConstraintLayout info_bottom_sheet, bottom_layout, info_playBtn, info_addLayout;
    private TextView info_title, info_day, info_content;
    private ProgressBar bottom_progressbar;
    private CardView info_imageLayout;

    //??????
    private ConstraintLayout viewer_kidsLayout, filterBtn, viewer_adultsLayout, filter_layout;
    private TextView filter_cancelBtn, filter_clearBtn, kids_text, adults_text, filterAdaptBtn;
    private String viewer, entertainment;
    private boolean kids, adult,original;
    private MaterialCheckBox kids_check, adults_check;
    private RadioGroup entertainment_radioGroup,original_radioGroup;

    //???????????? ??????
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
        //????????? ?????????
        initData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_page);

        //????????? ?????? ??????
        getWindow().setNavigationBarColor(Color.parseColor("#282828"));

        //Firebase
        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //????????????
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

        //?????? ????????? ?????? ???
        filter_search = findViewById(R.id.filter_search);
        filter_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Filter.this, Search.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
            }
        });

        //????????? ????????? ?????? ???
        filter_profile = findViewById(R.id.filter_profile);
        filter_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Filter.this, ProfileEtc.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
            }
        });


        //?????? ?????????
        filter_recyclerView = findViewById(R.id.filter_recyclerView);
        filter_scrollview = findViewById(R.id.filter_scrollview);

        //??????
        filter_layout = findViewById(R.id.filter_layout);
        filter_cancelBtn = findViewById(R.id.filter_cancelBtn);
        filter_clearBtn = findViewById(R.id.filter_clearBtn);
        filterBtn = findViewById(R.id.filterBtn);
        filterAdaptBtn = findViewById(R.id.filterAdaptBtn);

        //?????? ?????? ?????? ???
        filter_cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filter_layout.setVisibility(View.INVISIBLE);
            }
        });

        //????????? ?????? ?????? ???
        filter_clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adults_check.setChecked(false);
                kids_check.setChecked(false);
                viewerType();
                entertainment_radioGroup.check(R.id.entertainment_all);
                entertainment = "all";
                original_radioGroup.check(R.id.original_all);
                original = false;
            }
        });


        //?????? ?????? ?????? ???
        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filter_layout.setVisibility(View.VISIBLE);
            }
        });

        //?????? ?????? ?????? ?????? ???
        filterAdaptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filter_layout.setVisibility(View.INVISIBLE);
                adaptData(viewer, entertainment,original);

            }
        });

        //?????????
        viewer_kidsLayout = findViewById(R.id.viewer_kidsLayout);
        kids_check = findViewById(R.id.kids_check);
        kids_text = findViewById(R.id.kids_text);
        viewer_adultsLayout = findViewById(R.id.viewer_adultsLayout);
        adults_check = findViewById(R.id.adults_check);
        adults_text = findViewById(R.id.adults_text);
        adult = false;
        kids = false;
        viewer = "all";

        //????????? ???????????? ?????? ???
        viewer_kidsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (kids_check.isChecked() == false) {
                    kids = true;
                    kids_check.setChecked(true);
                    kids_text.setTextSize(18);
                    viewerType();
                } else {
                    kids = false;
                    kids_check.setChecked(false);
                    kids_text.setTextSize(16);
                    viewerType();
                }
            }
        });

        //?????? ???????????? ?????? ???
        viewer_adultsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adults_check.isChecked() == false) {
                    adult = true;
                    adults_check.setChecked(true);
                    adults_text.setTextSize(18);
                    viewerType();
                } else {
                    adult = false;
                    adults_check.setChecked(false);
                    adults_text.setTextSize(16);
                    viewerType();
                }
            }
        });


        //??????????????????
        entertainment_radioGroup = findViewById(R.id.entertainment_radioGroup);
        entertainment = "all";
        //?????????????????? ?????? ??? ????????? ?????? ?????? ???
        entertainment_radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.entertainment_series:
                        entertainment = "series";
                        break;
                    case R.id.entertainment_movie:
                        entertainment = "movie";
                        break;
                    case R.id.entertainment_all:
                        entertainment = "all";
                        break;
                }
            }
        });

        //?????? ????????????
        original_radioGroup = findViewById(R.id.original_radioGroup);
        original = false;
        //?????? ?????? ??? ????????? ?????? ?????? ???
        original_radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.original_only:
                        original = true;
                        break;
                    case R.id.original_all:
                        original = false;
                        break;
                }
            }
        });



        //????????????
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

        //????????? ?????? ??????
        info_cancel = findViewById(R.id.info_cancel);
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


    }

    private void initData() {

       


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

                    //????????? ??????
                    movieAdapter.setItems(data);

                    if(!data.isEmpty()){
                        //????????? ?????? ????????? ?????? ??????
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
                                Glide.with(Filter.this).load(uri).centerCrop().listener(new RequestListener<Drawable>() {
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
                        if (document.getBoolean("isAdd") == null) {
                            //+ ???????????? ??????
                            info_ic_add.setImageResource(R.drawable.ic_add);
                        } else if (document.getBoolean("isAdd") == false) {
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
                            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(Filter.this, R.style.MyAlertDialogStyle);

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
                            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(Filter.this, R.style.MyAlertDialogStyle);

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
                        if (document.getBoolean("isAdd") == null) {
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
                        } else if ((document.getBoolean("isAdd") == false)) {
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

    //????????? value ?????? ?????????
    private void viewerType() {
        //kids??? adult ?????? ?????? viewer type ??????
        if (kids == false && adult == false) {
            viewer = "all";
        } else if (kids == true && adult == false) {
            viewer = "kids";
        } else if (kids == false && adult == true) {
            viewer = "adult";
        } else if (kids == true && adult == true) {
            viewer = "all";
        }
    }

    //?????? ?????? ?????????
    private void adaptData(String viewerType, String entertainment,boolean isOriginal) {

        ArrayList<MovieItem> data = new ArrayList<>();
        MovieAdapter movieAdapter = new MovieAdapter();

        filter_recyclerView.setLayoutManager(new GridLayoutManager(Filter.this, 3));

        //???????????? ?????? ???????????? ??????
        if(viewer.equals("all")){
            //?????? ???????????? ??????
            if(original == false){
                fStore.collection("video").whereEqualTo("allView",false).whereArrayContains("entertainment", entertainment).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                            //????????? ??????
                            movieAdapter.setItems(data);
                        }
                    }
                });
                fStore.collection("video").whereEqualTo("allView",true).whereArrayContains("entertainment", entertainment).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                            //????????? ??????
                            movieAdapter.setItems(data);
                        }
                    }
                });
            }//??????????????? ????????? ??????
            else{
                fStore.collection("video").whereEqualTo("allView",false).whereArrayContains("entertainment", entertainment).whereEqualTo("original",original).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                            //????????? ??????
                            movieAdapter.setItems(data);
                        }
                    }
                });
                fStore.collection("video").whereEqualTo("viewer",viewer).whereEqualTo("allView",true).whereArrayContains("entertainment", entertainment).whereEqualTo("original",original).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                            //????????? ??????
                            movieAdapter.setItems(data);
                        }
                    }
                });
            }

            //kids??? adult??? ??????
        }else{
            //?????? ???????????? ??????
            if(original == false){
                fStore.collection("video").whereEqualTo("viewer",viewerType).whereArrayContains("entertainment", entertainment).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                            //????????? ??????
                            movieAdapter.setItems(data);
                        }
                    }
                });
            }//??????????????? ??????
            else{
                fStore.collection("video").whereEqualTo("viewer",viewerType).whereArrayContains("entertainment", entertainment).whereEqualTo("original",isOriginal).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

                            //????????? ??????
                            movieAdapter.setItems(data);


                        }
                    }
                });
            }
        }

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
}