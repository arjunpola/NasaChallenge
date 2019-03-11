package com.app.nasachallenge.network;

import com.app.nasachallenge.data.SearchItem;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NasaService {

    String NASA_IMAGE_SEARCH_BASE_URL = "https://images-api.nasa.gov";

    @GET("/search?media_type=image")
    Observable<List<SearchItem>> searchImages(@Query("q") String searchQuery, @Query("page") int page);

    @GET("/search?media_type=image&page=1")
    Observable<List<SearchItem>> searchImages(@Query("q") String searchQuery);
}
