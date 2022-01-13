package com.example.widdy.begin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.widdy.R;

public class Second extends Fragment {

    private ImageView second_image;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.begin_second, container, false);

        //이미지
        second_image = view.findViewById(R.id.second_image);

        //글라이드 적용
        Glide.with(this).load(R.drawable.second_image).centerCrop().into(second_image);

        return view;
    }
}