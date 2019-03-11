package com.app.nasachallenge;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class SearchResultViewHolder extends RecyclerView.ViewHolder {

    private TextView titleView;
    private ImageView thumbnailView;

    public SearchResultViewHolder(@NonNull View itemView) {
        super(itemView);
        titleView = itemView.findViewById(R.id.title);
        thumbnailView = itemView.findViewById(R.id.thumbnail);
    }

    public void setTitle(String title) {
        titleView.setText(title);
    }

    public void setImage(String imageUrl) {
        Picasso.with(thumbnailView.getContext())
                .load(imageUrl)
                .into(thumbnailView);
    }
}
