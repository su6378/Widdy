package com.example.widdy.begin;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.example.widdy.profile.Profile;
import com.example.widdy.R;
import com.example.widdy.register.Register;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import me.relex.circleindicator.CircleIndicator3;

public class Begin extends AppCompatActivity {
    private Toolbar toolbar;
    private ViewPager2 viewPager;
    private int num_page = 4;
    private CircleIndicator3 begin_indicator;
    private Button registerBtn;
    private FirebaseAuth mAuth;


    //뒤로가기
    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.begin_page);

        //하단바 색깔 적용
        getWindow().setNavigationBarColor(Color.parseColor("#282828"));

        //툴바
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //viewPager, indicator

        viewPager = findViewById(R.id.viewPager);
        begin_indicator = findViewById(R.id.begin_indicator);

        begin_indicator.setViewPager(viewPager);
        begin_indicator.createIndicators(num_page, 0);


        BeginVPagerAdapter viewPager2Adapter = new BeginVPagerAdapter(this, num_page);
        viewPager.setAdapter(viewPager2Adapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);

                if (positionOffsetPixels == 0) {
                    viewPager.setCurrentItem(position);
                }
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                begin_indicator.animatePageSelected(position % num_page);
            }


        });

        //회원가입 페이지 이동
        registerBtn = findViewById(R.id.registerBtn);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Begin.this, Register.class);
                startActivity(intent);
                overridePendingTransition( R.anim.slide_up_in, R.anim.slide_up_out );
            }
        });





    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.begin_overflow, menu);

        return true;
    }

    //아이템 선택시 실행
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.overflow_personal_info:
                return true;
            case R.id.overflow_logIn:
                Intent intent = new Intent(Begin.this, LogIn.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //로그인시 홈 페이지로 이동
    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            //user is already connected  so we need to redirect him to home page
            Intent intent = new Intent(Begin.this, Profile.class);
            startActivity(intent);
            finish();
        }
    }
}