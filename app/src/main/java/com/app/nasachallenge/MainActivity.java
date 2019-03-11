package com.app.nasachallenge;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

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

public class MainActivity extends AppCompatActivity {

    public static String TAG = "SEARCH ACTIVITY";

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NasaService service = getNasaService();

        compositeDisposable.add(service.searchImages("mars")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        searchItems -> {
                            if (searchItems.size() <= 0) {
                                Log.d(TAG, "Empty Search Results");
                            } else {
                                Log.d(TAG, searchItems.get(0).getTitle());
                            }
                        },
                        Throwable::printStackTrace));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
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
