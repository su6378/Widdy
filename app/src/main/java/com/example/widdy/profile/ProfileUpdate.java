package com.example.widdy.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.widdy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class ProfileUpdate extends AppCompatActivity {

    private ImageView backBtn;
    private TextInputEditText profile_name;
    private TextInputLayout profile_nameLayout;
    private TextView profile_update_text3;
    private SwitchMaterial update_switch, update_switch2;
    private AppCompatButton deleteBtn;
    private ProgressBar delete_progress;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore fStore;

    //뒤로가기 버튼
    @Override
    public void onBackPressed() {
        //닉네임 변경된 값 저장하기
        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        String nickname = profile_name.getText().toString().trim();

        fStore.collection("user").document(currentUser.getEmail()).update("nickname",nickname)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            fStore.collection("user").document(currentUser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                        DocumentSnapshot document = task.getResult();
                                        if(document.exists()){
                                            String nickname2 = document.getString("nickname");
                                            if(nickname.equals(nickname2)){
                                                Intent intent = new Intent();
                                                setResult(RESULT_OK, intent);
                                                finish();
                                                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    }
                });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_update_page);

        //하단바 색깔 적용
        getWindow().setNavigationBarColor(Color.parseColor("#282828"));

        Intent intent = getIntent();
        final String nickname = intent.getStringExtra("nickname");


        //프로필 이름
        profile_name = findViewById(R.id.profile_name);
        profile_nameLayout = findViewById(R.id.profile_nameLayout);
        profile_name.setText(nickname);
        profile_nameLayout.setHint("");

        //프로필 공백 시 에러
        profile_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                profile_nameLayout.setError("");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String name = profile_name.getText().toString().trim();
                if (name.isEmpty()) {
                    profile_nameLayout.setError("죄송합니다.이름은 반드시 입력하셔야 합니다.");
                } else {
                    profile_nameLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //계정 설정 색깔 변경
        profile_update_text3 = findViewById(R.id.profile_update_text3);

        //특정 문자 컬러 바꾸기
        String text = profile_update_text3.getText().toString().trim();

        SpannableString spannableString = new SpannableString(text);

        String word = "계정 설정";
        int start = text.indexOf(word);
        int end = start + word.length();

        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#009DFF")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        profile_update_text3.setText(spannableString);

        //스위치 on, off
        update_switch = findViewById(R.id.update_switch);
        update_switch2 = findViewById(R.id.update_switch2);

        update_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (update_switch.isChecked()) {
                    update_switch.setTrackTintList(ColorStateList.valueOf(Color.parseColor("#009DFF")));
                } else {
                    update_switch.setTrackTintList(ColorStateList.valueOf(Color.parseColor("#B4B4B4")));
                }
            }
        });

        update_switch2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (update_switch2.isChecked()) {
                    update_switch2.setTrackTintList(ColorStateList.valueOf(Color.parseColor("#009DFF")));
                } else {
                    update_switch2.setTrackTintList(ColorStateList.valueOf(Color.parseColor("#B4B4B4")));
                }
            }
        });

        //프로필 삭제
        deleteBtn = findViewById(R.id.deleteBtn);
        delete_progress = findViewById(R.id.delete_progress);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog.Builder alt_bld = new AlertDialog.Builder(ProfileUpdate.this, R.style.MyAlertDialogStyle);

                alt_bld.setTitle("프로필삭제")


                        .setMessage("\n프로필을 삭제하시겠습니까?")

                        .setCancelable(false)

                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                deleteBtn.setVisibility(View.INVISIBLE);
                                delete_progress.setVisibility(View.VISIBLE);

                                fStore = FirebaseFirestore.getInstance();
                                mAuth = FirebaseAuth.getInstance();
                                currentUser = mAuth.getCurrentUser();

                                //가입이 진행중일 때 터치 막기
                                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                Map<String, Object> user = new HashMap<>();
                                user.put("nickname", FieldValue.delete());
                                user.put("image", FieldValue.delete());

                                //데이터 삭제
                                fStore.collection("user").document(currentUser.getEmail())
                                        .update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            //삭제 완료시 프로그레스바가 사라지고 로그인페이지로 이동동
                                            delete_progress.setVisibility(View.INVISIBLE);
                                            Intent intent = new Intent(ProfileUpdate.this, Profile.class);
                                            startActivity(intent);
                                            finish();
                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                        }
                                    }
                                });
                            }

                        }).setNegativeButton("취소", null);

                AlertDialog alert = alt_bld.create();

                alert.show();
            }
        });

        //뒤로가기
        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //닉네임 변경된 값 저장하기
                fStore = FirebaseFirestore.getInstance();
                mAuth = FirebaseAuth.getInstance();
                currentUser = mAuth.getCurrentUser();
                String nickname = profile_name.getText().toString().trim();

                fStore.collection("user").document(currentUser.getEmail()).update("nickname",nickname)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    fStore.collection("user").document(currentUser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.isSuccessful()){
                                                DocumentSnapshot document = task.getResult();
                                                if(document.exists()){
                                                    String nickname2 = document.getString("nickname");
                                                    if(nickname.equals(nickname2)){
                                                        Intent intent = new Intent();
                                                        setResult(RESULT_OK, intent);
                                                        finish();
                                                        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
                                                    }
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        });
            }
        });
    }

}
/*//auth 삭제
FirebaseAuth.getInstance().signOut();*/