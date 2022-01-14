package com.example.widdy.register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.widdy.profile.Profile;
import com.example.widdy.LogIn;
import com.example.widdy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Register2 extends AppCompatActivity {
    private TextView loginBtn,privacy_error,privacy_policy_content;
    private TextInputLayout register_emailLayout,register_passwordLayout;
    private TextInputEditText register_email,register_password;
    private CheckBox privacy_policy;
    private Button nextBtn;
    private ProgressBar register_progress;
    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;

    //비밀번호 정규식 패턴
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[A-Za-z])" +

                    "(?=.*[0-9])" +

                    "(?=.*[@#$%^&+=])" +     // at least 1 special character

                    "(?=\\S+$)" +                     // no white spaces

                    ".{8,}" +                              // at least 4 characters

                    "$");

    //뒤로가기 버튼
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register2_page);

        //이메일 불러오기
        register_email = findViewById(R.id.register_email);
        register_emailLayout = findViewById(R.id.register_emailLayout);

        //개인정보처리방침 텍스트 컬러
        privacy_policy_content = findViewById(R.id.privacy_policy_content);

        initData();

        //이메일
        register_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validateEmail();
                if(validateEmail()==true){
                    register_emailLayout.setError("");
                    register_emailLayout.setHelperText("");
                    register_emailLayout.setBoxStrokeColor(Color.parseColor("#52E252"));
                }else{
                    register_emailLayout.setError("올바르지 않은 이메일 형식입니다.");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //비밀번호
        register_passwordLayout = findViewById(R.id.register_passwordLayout);
        register_password = findViewById(R.id.register_password);

        //비밀번호
        register_password.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (validatePassword() == true) {
                    register_passwordLayout.setErrorEnabled(false);
                    register_passwordLayout.setBoxStrokeColor(Color.parseColor("#52E252"));
                } else {
                    register_passwordLayout.setError("최소 8자리이상 숫자 영문자 특수문자를 조합해주세요.");
                }
            }
        });

        //체크박스
        privacy_policy = findViewById(R.id.privacy_policy);
        privacy_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(privacy_policy.isChecked()){

                    privacy_policy.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#009DFF")));
                    privacy_error.setVisibility(View.GONE);
                }
            }
        });



        //로그인 화면으로 이동
        loginBtn = findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Register2.this, LogIn.class);
                startActivity(intent);
            }
        });

        //다음페이지로 이동

        nextBtn = findViewById(R.id.nextBtn);
        register_progress = findViewById(R.id.register_progress);
        privacy_error = findViewById(R.id.privacy_error);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(privacy_policy.isChecked() && validatePassword()==true){
                    nextBtn.setText("");
                    register_progress.setVisibility(View.VISIBLE);

                    //가입이 진행중일 때 터치 막기
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    String email = register_email.getText().toString().trim();
                    String password = register_password.getText().toString().trim();


                    createUserAccount(email,password);

                }
                else{
                    privacy_policy.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#CD0000")));
                    privacy_error.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    //이전페이지 데이터 가져오기
    private void initData(){

        Intent intent = getIntent();

        final String email = intent.getStringExtra("email");

        register_email.setText(email);
        register_emailLayout.setBoxStrokeColor(Color.parseColor("#52E252"));

        //특정 문자 컬러 바꾸기
        String text = privacy_policy_content.getText().toString().trim();

        SpannableString spannableString = new SpannableString(text);

        String word = "개인정보 처리방침";
        int start = text.indexOf(word);
        int end = start + word.length();

        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#009DFF")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        privacy_policy_content.setText(spannableString);


    }

    //이메일 정규식
    private boolean validateEmail(){
        String email = register_email.getText().toString();
        Pattern pattern = Patterns.EMAIL_ADDRESS;

        if(pattern.matcher(email).matches()){
            return true;
        } else {
            return false;
        }
    }

    //비밀번호 정규식 메소드
    private boolean validatePassword() {
        String password = register_password.getText().toString().trim();
        register_passwordLayout = findViewById(R.id.register_passwordLayout);
        if (password.isEmpty()) {
            return false;
        }
        else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            return false;
        } else {
            return true;
        }
    }

    //FireAuth 회원가입
    private void createUserAccount(String email, String password) {
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            //user 컬렉션에 데이터 삽입
                            DocumentReference documentReference = fStore.collection("user").document(email);

                            Map<String, Object> user = new HashMap<>();
                            user.put("email", email);
                            user.put("password", password);

                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                //삽입 성공 시
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //데이터 베이스 추가 완료 후 홈 화면으로 이동
                                    Intent intent = new Intent(Register2.this, Profile.class);
                                    startActivity(intent);
                                    finish();
                                    //가입완료시
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            });

                        } else {

                        }
                    }
                });
    }
}