package com.example.widdy.begin;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.widdy.R;

public class Splash extends AppCompatActivity {

    private ImageView splash_image;
    private TextView splash_text;

    private Animation anim_ball;
    private Animation anim_fadeIn;

    //뒤로가기 막기
    @Override
    public void onBackPressed() {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_page);

        //하단바 색깔 적용
        getWindow().setNavigationBarColor(Color.parseColor("#282828"));

        //애니메이션
        anim_ball= AnimationUtils.loadAnimation(this, R.anim.splash_ball);
        anim_fadeIn= AnimationUtils.loadAnimation(this, R.anim.splash_fadein);

        //이미지 애니메이션
        splash_image = findViewById(R.id.splash_image);

        //텍스트 애니메이션
        splash_text = findViewById(R.id.splash_text);

        //통통 튀는 애니메이션
        anim_ball.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //페이드인 애니메이션
        anim_fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity(new Intent(Splash.this, Begin.class));

                Splash.this.finish();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //애니메이션 적용
        splash_image.setAnimation(anim_ball);
        splash_text.setAnimation(anim_fadeIn);
        //sdasdsad

    }
}