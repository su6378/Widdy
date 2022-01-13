package com.example.widdy.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import com.example.widdy.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ProfileCreate extends AppCompatActivity {

    private TextInputLayout profile_nameLayout;
    private TextInputEditText profile_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_create_page);

        //프로필 이름
        profile_nameLayout = findViewById(R.id.profile_nameLayout);
        profile_name = findViewById(R.id.profile_name);

        profile_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String name = profile_name.getText().toString().trim();
                if(name.isEmpty()){
                    profile_nameLayout.setError("죄송합니다.이름은 반드시 입력하셔야 합니다.");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    private void initData(){
        profile_nameLayout.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
    }
}