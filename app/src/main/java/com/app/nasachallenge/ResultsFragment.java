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
import com.app.nasachallenge.data.SearchViewModel;
import com.app.nasachallenge.listeners.OnResultItemClickListener;
import com.app.nasachallenge.listeners.OnSearchComplete;

public class ResultsFragment extends Fragment implements OnSearchComplete {

    private SearchViewModel searchModel;
    private ResultsAdapter resultsAdapter;
    private boolean isLoading;

    private View progressBar;
    private LinearLayoutManager layoutManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.results_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        OnResultItemClickListener resultItemClickListener = (OnResultItemClickListener) getActivity();
        RecyclerView resultsList = view.findViewById(R.id.results_list);
        progressBar = view.findViewById(R.id.progress_circular);
        resultsList.addItemDecoration(new DividerItemDecoration(resultsList.getContext(), DividerItemDecoration.VERTICAL));
        if (getActivity() != null) {
            searchModel = ViewModelProviders.of(getActivity()).get(SearchViewModel.class);
            resultsAdapter = new ResultsAdapter(searchModel.getSearchItems(), resultItemClickListener);
            layoutManager = new LinearLayoutManager(getActivity());
            resultsList.setLayoutManager(layoutManager);
            resultsList.setAdapter(resultsAdapter);
            resultsList.addOnScrollListener(recyclerViewOnScrollListener);

            //Optimization for Picasso image loads
            resultsList.setHasFixedSize(true);
            resultsList.setItemViewCacheSize(20);
        }
    }

    private RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (layoutManager != null) {
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        progressBar.setVisibility(View.VISIBLE);
                        searchModel.fetchNextPage(ResultsFragment.this);
                    }
                }
            }
        }
    };

    public void refresh() {
        if (resultsAdapter != null) {
            resultsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void searchCompleted(String err) {
        isLoading = false;
        progressBar.setVisibility(View.GONE);
    }
}
