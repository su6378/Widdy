package com.example.widdy.register;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.widdy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    private ImageView cancelBtn;
    private TextInputLayout register_emailLayout;
    private TextInputEditText register_email;
    private Button nextBtn;
    private boolean emailcheck = false;
    private FirebaseFirestore fStore;

    //뒤로가기 버튼
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition( R.anim.slide_down_in, R.anim.slide_down_out );

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page);

        //하단바 색깔 적용
        getWindow().setNavigationBarColor(Color.parseColor("#282828"));

        //취소하기
        cancelBtn = findViewById(R.id.cancelBtn);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                overridePendingTransition( R.anim.slide_down_in, R.anim.slide_down_out );

            }
        });

        //이메일
        register_emailLayout = findViewById(R.id.register_emailLayout);
        register_email = findViewById(R.id.register_email);

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



        //다음화면으로 이동
        nextBtn = findViewById(R.id.nextBtn);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(validateEmail()==true){
                    emailCheck();

                }else{

                }
            }
        });
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

    //이메일 중복 체크 메소드
    private void emailCheck() {

        fStore = FirebaseFirestore.getInstance();


        String email = register_email.getText().toString().trim();
        if (email.isEmpty()) {
            register_emailLayout.setError("이메일을 입력해주세요.");

        } else {

            fStore.collection("user").document(email)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            register_emailLayout.setError("이미 사용중인 아이디입니다.");
                        } else {
                            emailcheck = true;
                            Intent intent = new Intent(Register.this, Register2.class);
                            intent.putExtra("email", email);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                    }
                }
            });
        }
    }
}