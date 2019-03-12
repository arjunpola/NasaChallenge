package com.app.nasachallenge.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.app.nasachallenge.listeners.OnSearchComplete;
import com.app.nasachallenge.network.NasaService;
import com.app.nasachallenge.network.SearchResponseConverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchViewModel extends ViewModel {

    private MutableLiveData<List<SearchItem>> searchResults = new MutableLiveData<>();
    private int mPageNo = 1;
    private String mQuery = "";

    private NasaService service = getNasaService();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private LiveData<List<SearchItem>> performSearch(String query, int requestedPage, OnSearchComplete onSearchComplete) {
        if (isValidSearch(query, requestedPage)) {
            refreshDataIfNecessary(query);
            compositeDisposable.add(service.searchImages(query)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            searchItems -> {
                                if (searchItems.size() <= 0) {
                                    onSearchComplete.searchCompleted("Empty Search Results");
                                } else {
                                    mQuery = query;
                                    mPageNo = requestedPage;
                                    List<SearchItem> curr = searchResults.getValue();
                                    if (curr == null) {
                                        curr = new ArrayList<>();
                                    }
                                    curr.addAll(searchItems);
                                    searchResults.postValue(curr);
                                    onSearchComplete.searchCompleted("");
                                }
                            }, err -> onSearchComplete.searchCompleted(err.getLocalizedMessage())));
        } else {
            onSearchComplete.searchCompleted("");
        }
        return searchResults;
    }

    public LiveData<List<SearchItem>> performSearch(String query, OnSearchComplete onSearchComplete) {
        return performSearch(query, 1, onSearchComplete);
    }

    public LiveData<List<SearchItem>> fetchNextPage(OnSearchComplete onSearchComplete) {
        return performSearch(mQuery, mPageNo + 1, onSearchComplete);
    }

    // TODO: Perform full pagination if time permits
    public LiveData<List<SearchItem>> fetchPreviousPage(OnSearchComplete onSearchComplete) {
        return performSearch(mQuery, mPageNo - 1, onSearchComplete);
    }

    public List<SearchItem> getSearchItems() {
        return searchResults.getValue();
    }

    public Optional<SearchItem> getSearchFirstItem() {
        List<SearchItem> items = searchResults.getValue();
        if (items != null && items.size() > 0) {
            return Optional.of(items.get(0));
        }

        return Optional.empty();
    }

    private boolean isValidSearch(String query, int requestedPage) {
        // invalid search
        if (requestedPage <= 0 || requestedPage >= 100 || query == null || query.trim().isEmpty()) {
            return false;
        }
        // Results already cached
        if (mQuery.equals(query) && searchResults.getValue() != null &&
                searchResults.getValue().size() >= mPageNo * 100 && requestedPage == mPageNo) {
            return false;
        }
        return true;
    }

    private void refreshDataIfNecessary(String query) {
        if (!mQuery.equals(query) && searchResults.getValue() != null) {
            searchResults = new MutableLiveData<>();
        }
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
}
