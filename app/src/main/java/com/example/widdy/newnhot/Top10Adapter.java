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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.widdy.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class Top10Adapter extends RecyclerView.Adapter<Top10Adapter.Top10ViewHolder> {

    private ArrayList<Top10Item> items = new ArrayList<>();

    @NonNull
    @Override
    public Top10Adapter.Top10ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.top10_item, parent, false);
        Top10ViewHolder viewHolder = new Top10ViewHolder(itemView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Top10Adapter.Top10ViewHolder viewHolder, int position) {
        Top10Item item = items.get(position);

        viewHolder.top10_movie_progressbar.setVisibility(View.VISIBLE);
        viewHolder.top10_movie_image.setVisibility(View.INVISIBLE);
        viewHolder.top10_playBtn.setVisibility(View.INVISIBLE);

        //item 저장된 value 값 불러오기
        viewHolder.top10_ranking.setText(item.getRanking());
        viewHolder.top10_title.setText(item.getTitle());
        viewHolder.top10_subTitle.setText(item.getSubTitle());
        viewHolder.top10_detail_text.setText(item.getDetail());
        viewHolder.top10_open_tag.setText(item.getTag());

        if(item.getSubTitle().equals("없음")){
            viewHolder.top10_subTitle.setVisibility(View.GONE);
        }


        //이미지 불러오기
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://widdy-3d0ed.appspot.com");
        StorageReference storageRef = storage.getReference();
        storageRef.child("top10/" +item.getUrl()+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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
                        viewHolder.top10_movie_progressbar.setVisibility(View.INVISIBLE);
                        viewHolder.top10_movie_image.setVisibility(View.VISIBLE);
                        viewHolder.top10_playBtn.setVisibility(View.VISIBLE);
                        Log.d("테스트","성공");
                        return false;
                    }
                }).into(viewHolder.top10_movie_image);

            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(ArrayList<Top10Item> items) {
        this.items = items;
    }

    public class Top10ViewHolder extends RecyclerView.ViewHolder {

        ImageView top10_movie_image,top10_playBtn;
        ProgressBar top10_movie_progressbar;
        TextView  top10_ranking,top10_title, top10_subTitle, top10_detail_text,top10_open_tag;

        Top10ViewHolder(View itemView) {
            super(itemView);

            top10_ranking = itemView.findViewById(R.id.top10_ranking);
            top10_movie_image = itemView.findViewById(R.id.top10_movie_image);
            top10_movie_progressbar = itemView.findViewById(R.id.top10_movie_progressbar);
            top10_playBtn = itemView.findViewById(R.id.top10_playBtn);
            top10_title = itemView.findViewById(R.id.top10_title);
            top10_subTitle = itemView.findViewById(R.id.top10_subTitle);
            top10_detail_text = itemView.findViewById(R.id.top10_detail_text);
            top10_open_tag = itemView.findViewById(R.id.top10_open_tag);



        }
    }
}
