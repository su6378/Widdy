package com.example.widdy.main;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.widdy.R;
import com.example.widdy.profile.Profile;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class PlayingAdapter extends RecyclerView.Adapter<PlayingAdapter.ViewHolder> {

    private ArrayList<PlayingItem> items = new ArrayList<>();
    private OnItemClickListner listner;

    public interface OnItemClickListner {
        void OnItemClick(ViewHolder holder, View view, int position);
    }



    @NonNull
    @Override
    public PlayingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.playing_recycler_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PlayingAdapter.ViewHolder viewHolder, int position) {
        PlayingItem item = items.get(position);

        viewHolder.playing_progressbar.setVisibility(View.VISIBLE);
        viewHolder.playing_image.setVisibility(View.INVISIBLE);
        viewHolder.ic_playing.setVisibility(View.INVISIBLE);

        viewHolder.playing_id.setText(item.getUrl());


        //이미지 불러오기
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://widdy-3d0ed.appspot.com");
        StorageReference storageRef = storage.getReference();
        storageRef.child("movie/" +item.getUrl()+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(viewHolder.itemView.getContext()).load(uri).centerCrop().listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.d("테스트","실패");
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        viewHolder.playing_progressbar.setVisibility(View.INVISIBLE);
                        viewHolder.playing_image.setVisibility(View.VISIBLE);
                        viewHolder.ic_playing.setVisibility(View.VISIBLE);
                        return false;
                    }
                }).into(viewHolder.playing_image);

            }
        });



    }
    public void OnItemClick(ViewHolder holder, View view, int position) {
        if (listner != null) {
            listner.OnItemClick(holder, view, position);
        }
    }

    public void setItemClickListener(OnItemClickListner listener) {
        this.listner = listener;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(ArrayList<PlayingItem> items) {
        this.items = items;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView playing_image;
        ImageView ic_playing,playing_info;
        ProgressBar playing_progressbar;
        TextView playing_id;

        ViewHolder(View itemView) {
            super(itemView);

            playing_image = itemView.findViewById(R.id.playing_image);
            ic_playing = itemView.findViewById(R.id.ic_playing);
            playing_progressbar = itemView.findViewById(R.id.playing_progressbar);
            playing_info = itemView.findViewById(R.id.playing_info);
            playing_id = itemView.findViewById(R.id.playing_id);

            playing_info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listner != null) {
                        listner.OnItemClick(ViewHolder.this, view, getAdapterPosition());
                    }
                }
            });



        }
    }
}