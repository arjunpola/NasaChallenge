package com.app.nasachallenge.data;

import android.support.annotation.NonNull;

public class SearchItem {

    @NonNull
    private String imageUrl;
    @NonNull
    private String title;
    @NonNull
    private String description;

    public SearchItem(@NonNull String imageUrl, @NonNull String title, @NonNull String description) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.description = description;
    }

    @NonNull
    public String getImageUrl() {
        return imageUrl;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    @NonNull
    public String getDescription() {
        return description;
    }
}
