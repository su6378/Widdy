package com.example.widdy.main;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Dimension;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.widdy.R;


public class Home extends Fragment {

    ImageView home_image;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_home, container, false);

        //홈 이미지
        home_image = view.findViewById(R.id.home_image);

        RequestOptions options = new RequestOptions();
        options.centerCrop();
        //글라이드 적용

        //Glide.with(this).load(R.drawable.home_image).apply(options).into(home_image);
        Glide.with(this).load(R.drawable.ic_test).centerCrop().into(home_image);





        return view;
    }
}