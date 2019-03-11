package com.app.nasachallenge;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class SearchFragment extends Fragment {

    private EditText searchQueryInput;
    private Button searchButton;
    private OnSearchListener onSearchListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.search_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchQueryInput = view.findViewById(R.id.search_query_input);
        searchButton = view.findViewById(R.id.search_btn);

        onSearchListener = (OnSearchListener) getActivity();
        searchButton.setOnClickListener(v -> {
            String searchText = searchQueryInput.getText().toString();
            onSearchListener.startSearch(searchText);
        });

    }
}
