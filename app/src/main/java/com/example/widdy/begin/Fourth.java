package com.example.widdy.begin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.widdy.R;

public class Fourth extends Fragment {

    private ImageView fourth_image;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.begin_third, container, false);

        //이미지
        fourth_image = view.findViewById(R.id.third_image);

        //글라이드 적용
        Glide.with(this).load(R.drawable.fourth_image).centerCrop().into(fourth_image);

        return view;
    }
}