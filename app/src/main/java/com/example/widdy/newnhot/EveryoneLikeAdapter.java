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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class EveryoneLikeAdapter extends RecyclerView.Adapter<EveryoneLikeAdapter.EveryoneLikeViewHolder> {

    private ArrayList<EveryoneLikeItem> items = new ArrayList<>();
    @NonNull
    @Override
    public EveryoneLikeAdapter.EveryoneLikeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.everyone_like_item, parent, false);
        EveryoneLikeViewHolder viewHolder = new EveryoneLikeViewHolder(itemView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EveryoneLikeAdapter.EveryoneLikeViewHolder viewHolder, int position) {
        EveryoneLikeItem item = items.get(position);

        viewHolder.el_movie_progressbar.setVisibility(View.VISIBLE);
        viewHolder.el_movie_image.setVisibility(View.INVISIBLE);
        viewHolder.el_playBtn.setVisibility(View.INVISIBLE);

        //item 저장된 value 값 불러오기
        viewHolder.everyone_like_title.setText(item.getTitle());
        viewHolder.everyone_like_subTitle.setText(item.getSubTitle());
        viewHolder.everyone_like_detail_text.setText(item.getDetail());
        viewHolder.everyone_like_open_tag.setText(item.getTag());

        if(item.getSubTitle().equals("없음")){
            viewHolder.everyone_like_subTitle.setVisibility(View.GONE);
        }


        //이미지 불러오기
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://widdy-3d0ed.appspot.com");
        StorageReference storageRef = storage.getReference();
        storageRef.child("everyonelike/" +item.getUrl()+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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
                        viewHolder.el_movie_progressbar.setVisibility(View.INVISIBLE);
                        viewHolder.el_movie_image.setVisibility(View.VISIBLE);
                        viewHolder.el_playBtn.setVisibility(View.VISIBLE);
                        return false;
                    }
                }).into(viewHolder.el_movie_image);

            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(ArrayList<EveryoneLikeItem> items) {
        this.items = items;
    }

    public class EveryoneLikeViewHolder extends RecyclerView.ViewHolder {

        ImageView el_movie_image,el_playBtn;
        ProgressBar el_movie_progressbar;
        TextView everyone_like_title,everyone_like_subTitle,everyone_like_detail_text,everyone_like_open_tag;

        EveryoneLikeViewHolder(View itemView) {
            super(itemView);

            el_movie_image = itemView.findViewById(R.id.el_movie_image);
            el_movie_progressbar = itemView.findViewById(R.id.el_movie_progressbar);
            el_playBtn = itemView.findViewById(R.id.el_playBtn);
            everyone_like_title = itemView.findViewById(R.id.everyone_like_title);
            everyone_like_subTitle = itemView.findViewById(R.id.everyone_like_subTitle);
            everyone_like_detail_text = itemView.findViewById(R.id.everyone_like_detail_text);
            everyone_like_open_tag = itemView.findViewById(R.id.everyone_like_open_tag);



        }
    }
}
