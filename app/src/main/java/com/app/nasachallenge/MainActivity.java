package com.app.nasachallenge;

import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.app.nasachallenge.data.SearchItem;
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

public class MainActivity extends FragmentActivity implements OnSearchListener {

    public static String TAG = "SEARCH ACTIVITY";

    SearchFragment searchFragment;
    ProgressBar progressBar;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private NasaService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progress_circular);
        searchFragment = new SearchFragment();

        // Initialize service object
        service = getNasaService();

        if (savedInstanceState == null) {
            // Add Search fragment
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, searchFragment)
                    .commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
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
                                if (searchItems.size() <= 0) {
                                    showSnackBar("Empty Search Results");
                                } else {
                                    Log.d(TAG, searchItems.get(0).getTitle());
                                }
                            }, err -> showSnackBar(err.getLocalizedMessage())));

            searchFragment.hideSearchButton();
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void showSnackBar(String message) {
        Snackbar.make(progressBar, message, Snackbar.LENGTH_SHORT)
                .show();
    }

    private NasaService getNasaService() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(Duration.ofSeconds(30))
                .callTimeout(Duration.ofSeconds(30))
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
}
