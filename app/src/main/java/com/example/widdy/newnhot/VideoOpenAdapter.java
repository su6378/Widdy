package com.example.widdy.newnhot;

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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.widdy.R;
import com.example.widdy.main.MovieAdapter;
import com.example.widdy.main.MovieItem;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class VideoOpenAdapter extends RecyclerView.Adapter<VideoOpenAdapter.VideoOpenViewHolder>{

    private ArrayList<VideoOpenItem> items = new ArrayList<>();

    @NonNull
    @Override
    public VideoOpenAdapter.VideoOpenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_open_item, parent, false);
        VideoOpenViewHolder viewHolder = new VideoOpenViewHolder(itemView);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull VideoOpenAdapter.VideoOpenViewHolder viewHolder, int position) {
        VideoOpenItem item = items.get(position);

        viewHolder.vo_movie_progressbar.setVisibility(View.VISIBLE);
        viewHolder.vo_movie_image.setVisibility(View.INVISIBLE);
        viewHolder.vo_playBtn.setVisibility(View.INVISIBLE);

        //item 저장된 value 값 불러오기
        viewHolder.video_open_month.setText(item.getMonth());
        viewHolder.video_open_day.setText(item.getDay());
        viewHolder.video_movie_title.setText(item.getTitle());
        viewHolder.video_open_title.setText(item.getOpenTitle());
        viewHolder.video_open_detail_text.setText(item.getDetailText());
        viewHolder.video_open_tag.setText(item.getTag());


        //이미지 불러오기
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://widdy-3d0ed.appspot.com");
        StorageReference storageRef = storage.getReference();
        storageRef.child("newnhot/" +item.getUrl()+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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
                        viewHolder.vo_movie_progressbar.setVisibility(View.INVISIBLE);
                        viewHolder.vo_movie_image.setVisibility(View.VISIBLE);
                        viewHolder.vo_playBtn.setVisibility(View.VISIBLE);
                        return false;
                    }
                }).into(viewHolder.vo_movie_image);

            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(ArrayList<VideoOpenItem> items) {
        this.items = items;
    }

    public class VideoOpenViewHolder extends RecyclerView.ViewHolder {

        ImageView vo_movie_image,vo_playBtn;
        ProgressBar vo_movie_progressbar;
        TextView video_open_month,video_open_day,video_movie_title,video_open_title,video_open_detail_text,video_open_tag;
        ConstraintLayout vo_movie_layout;

        VideoOpenViewHolder(View itemView) {
            super(itemView);

            video_open_month = itemView.findViewById(R.id.video_open_month);
            video_open_day = itemView.findViewById(R.id.video_open_day);
            vo_movie_image = itemView.findViewById(R.id.vo_movie_image);
            vo_movie_progressbar = itemView.findViewById(R.id.vo_movie_progressbar);
            vo_movie_layout = itemView.findViewById(R.id.vo_movie_layout);
            video_movie_title = itemView.findViewById(R.id.video_movie_title);
            video_open_title = itemView.findViewById(R.id.video_open_title);
            video_open_detail_text = itemView.findViewById(R.id.video_open_detail_text);
            video_open_tag = itemView.findViewById(R.id.video_open_tag);
            vo_playBtn = itemView.findViewById(R.id.vo_playBtn);

        }
    }
}
