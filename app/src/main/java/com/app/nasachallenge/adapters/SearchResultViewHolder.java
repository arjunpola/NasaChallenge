package com.app.nasachallenge.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.nasachallenge.R;
import com.squareup.picasso.Picasso;

class SearchResultViewHolder extends RecyclerView.ViewHolder {

    private View contentView;
    private TextView titleView;
    private ImageView thumbnailView;
    private Picasso picasso;

    SearchResultViewHolder(@NonNull View itemView) {
        super(itemView);
        contentView = itemView;
        titleView = itemView.findViewById(R.id.title);
        thumbnailView = itemView.findViewById(R.id.thumbnail);
        picasso = Picasso.with(itemView.getContext());
    }

    void setTitle(String title) {
        titleView.setText(title);
    }

    void setImage(String imageUrl) {
        picasso.load(imageUrl)
                .noPlaceholder()
                .fit()
                .centerCrop()
                .into(thumbnailView);
    }

    View getContentView() {
        return contentView;
    }
}
