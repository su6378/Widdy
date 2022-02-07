package com.example.widdy.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.widdy.R;
import com.example.widdy.profile.ProfileEtc;

public class NewnHot extends Fragment {

    private ImageView newhot_profile;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_newnhot, container, false);

        //프로필 기타 페이지로 이동
        newhot_profile = view.findViewById(R.id.newhot_profile);

        newhot_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ProfileEtc.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
            }
        });

        return view;
    }
}