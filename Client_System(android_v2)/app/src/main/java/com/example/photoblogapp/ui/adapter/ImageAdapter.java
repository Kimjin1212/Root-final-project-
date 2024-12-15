package com.example.photoblogapp.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.photoblogapp.R;
import com.example.photoblogapp.api.Api;
import com.example.photoblogapp.data.Photo;

import java.text.SimpleDateFormat;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    public interface ImageCallBack {
        void doClick(Photo photo);

        void doLongClick(Photo photo);
    }

    private List<Photo> images;
    private ImageCallBack onItemClickListener;
    Context context;

    public ImageAdapter(List<Photo> images, Context context) {
        this.images = images;
        this.context = context;
    }

    public void setOnItemClickListener(ImageCallBack listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(Api.base_media_httpaddr + images.get(position).image).into(holder.imageView);
        holder.textView.setText(images.get(position).tags.replace("_","     "));
        holder.createtime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(images.get(position).created_date));
        // 删除图片的长按事件
        holder.itemView.setOnLongClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.doLongClick(images.get(position));
            }
            return true;
        });

        // 添加标签的点击事件
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.doClick(images.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        TextView createtime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            textView = itemView.findViewById(R.id.tage);
            createtime = itemView.findViewById(R.id.createtime);
        }
    }
}
