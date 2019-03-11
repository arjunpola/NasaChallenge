package com.app.nasachallenge.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.nasachallenge.R;
import com.app.nasachallenge.SearchResultViewHolder;
import com.app.nasachallenge.data.SearchItem;

import java.util.ArrayList;
import java.util.List;

public class ResultsAdapter extends RecyclerView.Adapter<SearchResultViewHolder> {
    private List<SearchItem> searchResults;

    public ResultsAdapter(List<SearchItem> searchResults) {
        if (searchResults != null) {
            this.searchResults = searchResults;
        } else {
            this.searchResults = new ArrayList<>();
        }
    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View resultView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.result_item_layout, viewGroup, false);
        return new SearchResultViewHolder(resultView);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultViewHolder searchResultViewHolder, int i) {
        SearchItem item = searchResults.get(i);
        searchResultViewHolder.setTitle(item.getTitle());
        searchResultViewHolder.setImage(item.getImageUrl());
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }
}
