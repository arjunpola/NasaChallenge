package com.app.nasachallenge;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.nasachallenge.adapters.ResultsAdapter;
import com.app.nasachallenge.data.SearchItem;
import com.app.nasachallenge.data.SearchViewModel;
import com.app.nasachallenge.listeners.OnResultItemClickListener;

import java.util.List;

public class ResultsFragment extends Fragment {

    ResultsAdapter resultsAdapter;

    RecyclerView resultsList;
    OnResultItemClickListener resultItemClickListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.results_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        resultItemClickListener = (OnResultItemClickListener) getActivity();
        resultsList = view.findViewById(R.id.results_list);
        resultsList.addItemDecoration(new DividerItemDecoration(resultsList.getContext(), DividerItemDecoration.VERTICAL));
        if (getActivity() != null) {
            SearchViewModel searchModel = ViewModelProviders.of(getActivity()).get(SearchViewModel.class);
            resultsAdapter = new ResultsAdapter(searchModel.getSearchItems(), resultItemClickListener);
            resultsList.setLayoutManager(new LinearLayoutManager(getActivity()));
            resultsList.setAdapter(resultsAdapter);
        }
    }

    public void refresh() {
        if (resultsAdapter != null) {
            resultsAdapter.notifyDataSetChanged();
        }
    }
}
