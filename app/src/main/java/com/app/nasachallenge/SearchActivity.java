package com.app.nasachallenge;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.app.nasachallenge.data.SearchItem;
import com.app.nasachallenge.data.SearchViewModel;
import com.app.nasachallenge.listeners.OnResultItemClickListener;
import com.app.nasachallenge.listeners.OnSearchComplete;
import com.app.nasachallenge.listeners.OnSearchListener;

import java.util.Optional;

public class SearchActivity extends AppCompatActivity implements OnSearchListener, OnResultItemClickListener, OnSearchComplete {

    public static String SEARCH_FRAGMENT_TAG = "SEARCH FRAGMENT";
    public static String RESULTS_FRAGMENT_TAG = "RESULTS FRAGMENT";
    public static String DETAIL_FRAGMENT_TAG = "DETAIL FRAGMENT";

    private SearchFragment searchFragment;
    private ResultsFragment resultsFragment;
    private ProgressBar progressBar;
    private ViewGroup detailContainer;

    private FragmentManager mFragmentManager;
    private SearchViewModel searchModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progress_circular);
        detailContainer = findViewById(R.id.detail_container);

        searchFragment = new SearchFragment();
        resultsFragment = new ResultsFragment();
        mFragmentManager = getSupportFragmentManager();
        searchModel = ViewModelProviders.of(this).get(SearchViewModel.class);

        if (savedInstanceState == null) {
            mFragmentManager
                    .beginTransaction()
                    .add(R.id.fragment_container, searchFragment, SEARCH_FRAGMENT_TAG)
                    .commit();
        } else {
            SearchFragment searchFrag = (SearchFragment) mFragmentManager.findFragmentByTag(SEARCH_FRAGMENT_TAG);
            if (searchFrag != null) {
                searchFragment = searchFrag;
            }

            ResultsFragment resultsFrag = (ResultsFragment) mFragmentManager.findFragmentByTag(RESULTS_FRAGMENT_TAG);
            if (resultsFrag != null) {
                resultsFragment = resultsFrag;
            }

            DetailFragment detailFrag = (DetailFragment) mFragmentManager.findFragmentByTag(DETAIL_FRAGMENT_TAG);
            if (detailContainer != null && detailFrag != null) {
                detailContainer.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onBackPressed() {

        //Hide detailContainer on back pressed for large screens
        if (detailContainer != null &&
                mFragmentManager.findFragmentByTag(DETAIL_FRAGMENT_TAG) != null) {
            detailContainer.setVisibility(View.GONE);
            mFragmentManager.popBackStackImmediate();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void startSearch(String query) {
        if (query != null && !query.trim().isEmpty()) {
            searchFragment.hideSearchButton();
            progressBar.setVisibility(View.VISIBLE);
            searchModel.performSearch(query, this).observe(this, searchItems -> showResults());
        }
    }

    @Override
    public void searchCompleted(String err) {
        if (err != null && !err.isEmpty()) {
            showToast(err);
        }

        progressBar.setVisibility(View.GONE);
        searchFragment.showSearchButton();
    }

    @Override
    public void onResultItemClick(SearchItem searchItem) {
        DetailFragment detailFragment = new DetailFragment();
        detailFragment.setArguments(DetailFragment.getArguments(searchItem));

        FragmentManager fm = getSupportFragmentManager();
        if (detailContainer != null) {
            fm.beginTransaction()
                    .replace(R.id.detail_container, detailFragment, DETAIL_FRAGMENT_TAG)
                    .commit();
            detailContainer.setVisibility(View.VISIBLE);
        } else {
            fm.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.fragment_container, detailFragment, DETAIL_FRAGMENT_TAG)
                    .commit();
        }
    }

    private void showToast(String message) {
        Toast.makeText(SearchActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void showResults() {
        if (resultsFragment == null) {
            resultsFragment = new ResultsFragment();
        } else {
            resultsFragment.refresh();
        }

        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragment_container, resultsFragment, RESULTS_FRAGMENT_TAG)
                .commit();

        // By default present the first result item in detail layout for large screens
        Optional<SearchItem> item = searchModel.getSearchFirstItem();
        if (detailContainer != null && item.isPresent()) {
            onResultItemClick(item.get());
        }
    }
}
