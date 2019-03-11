package com.app.nasachallenge;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.app.nasachallenge.data.SearchItem;
import com.app.nasachallenge.listeners.OnResultItemClickListener;
import com.app.nasachallenge.listeners.OnSearchListener;
import com.app.nasachallenge.network.NasaService;
import com.app.nasachallenge.network.SearchResponseConverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends FragmentActivity implements OnSearchListener, OnResultItemClickListener {

    public static String TAG = "SEARCH ACTIVITY";
    public static String SEARCH_FRAGMENT_TAG = "SEARCH FRAGMENT";
    public static String RESULTS_FRAGMENT_TAG = "RESULTS FRAGMENT";
    public static String DETAIL_FRAGMENT_TAG = "DETAIL FRAGMENT";

    private SearchFragment searchFragment;
    private ResultsFragment resultsFragment;
    private ProgressBar progressBar;
    private ViewGroup detailContainer;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private NasaService service;
    private List<SearchItem> searchResults;
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progress_circular);
        detailContainer = findViewById(R.id.detail_container);

        searchFragment = new SearchFragment();
        resultsFragment = new ResultsFragment();
        mFragmentManager = getSupportFragmentManager();

        // Initialize service object
        service = getNasaService();

        if (savedInstanceState == null) {
            mFragmentManager
                    .beginTransaction()
                    .add(R.id.fragment_container, searchFragment, SEARCH_FRAGMENT_TAG)
                    .commit();
        } else {
            SearchFragment searchFrag = (SearchFragment) mFragmentManager.findFragmentByTag(SEARCH_FRAGMENT_TAG);

            if (searchFrag == null) {
                // Resets the view back to search fragment as the results data is not yet retained
                // TODO: Use ViewModel or parcel it in savedInstanceState
                mFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container, searchFragment, SEARCH_FRAGMENT_TAG)
                        .commit();
            } else {
                searchFragment = searchFrag;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

    @Override
    public void onBackPressed() {
        if (detailContainer == null && mFragmentManager.findFragmentByTag(DETAIL_FRAGMENT_TAG) != null) {
            mFragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void startSearch(String query) {
        if (query != null && !query.trim().isEmpty()) {
            compositeDisposable.add(service.searchImages(query)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doFinally(() -> {
                        progressBar.setVisibility(View.GONE);
                        searchFragment.showSearchButton();
                    })
                    .subscribe(
                            searchItems -> {
                                searchResults = searchItems;
                                if (searchItems.size() <= 0) {
                                    showSnackBar("Empty Search Results");
                                } else {
                                    showResults();
                                }
                            }, err -> showSnackBar(err.getLocalizedMessage())));

            searchFragment.hideSearchButton();
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResultItemClick(SearchItem searchItem) {
        DetailFragment detailFragment = new DetailFragment();
        detailFragment.setArguments(DetailFragment.getArguments(searchItem));

        FragmentManager fm = getSupportFragmentManager();
        if (detailContainer != null) {
            // TODO: Verify UI on large screens + landscape
            fm.beginTransaction()
                    .replace(R.id.detail_container, detailFragment, DETAIL_FRAGMENT_TAG)
                    .commit();
        } else {
            fm.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.fragment_container, detailFragment, DETAIL_FRAGMENT_TAG)
                    .commit();
        }
    }

    private void showSnackBar(String message) {
        Snackbar.make(progressBar, message, Snackbar.LENGTH_SHORT)
                .show();
    }

    private void showResults() {
        if (resultsFragment == null) {
            resultsFragment = new ResultsFragment();
        } else {
            resultsFragment.refresh();
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, resultsFragment, RESULTS_FRAGMENT_TAG)
                .commit();
    }

    private NasaService getNasaService() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(Duration.ofSeconds(30))
                .readTimeout(Duration.ofSeconds(30))
                .writeTimeout(Duration.ofSeconds(30))
                .addInterceptor(interceptor).build();

        GsonBuilder gsonBuilder = new GsonBuilder();
        JsonDeserializer<List<SearchItem>> deserializer = new SearchResponseConverter();

        Type searchItemsList = new TypeToken<List<SearchItem>>() {
        }.getType();
        gsonBuilder.registerTypeAdapter(searchItemsList, deserializer);
        Gson customGson = gsonBuilder.create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(NasaService.NASA_IMAGE_SEARCH_BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(customGson))
                .client(client)
                .build();

        return retrofit.create(NasaService.class);
    }

    protected List<SearchItem> getSearchResults() {
        return searchResults;
    }
}
