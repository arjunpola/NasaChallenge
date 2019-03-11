package com.app.nasachallenge.network;

import com.app.nasachallenge.data.SearchItem;
import com.app.nasachallenge.network.items.JsonInfo;
import com.app.nasachallenge.network.items.JsonLink;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SearchResponseConverter implements JsonDeserializer<List<SearchItem>> {

    @Override
    public List<SearchItem> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        Gson gson = new Gson();
        JsonObject collection = json.getAsJsonObject().getAsJsonObject("collection");

        // Parse Items
        JsonArray searchItems = collection.getAsJsonArray("items");
        Type jsonItemsListType = new TypeToken<List<JsonSearchItem>>() {
        }.getType();
        List<JsonSearchItem> searchItemsJsonList = gson.fromJson(searchItems, jsonItemsListType);

        List<SearchItem> searchItemsList = new ArrayList<>();
        for (JsonSearchItem item : searchItemsJsonList) {
            List<JsonInfo> infoList = item.infoList;
            List<JsonLink> linkList = item.links;
            if (infoList == null || linkList == null || infoList.size() == 0 || linkList.size() == 0) {
                continue;
            }

            searchItemsList.add(getSearchItem(item));
        }

        return searchItemsList;
    }

    private SearchItem getSearchItem(JsonSearchItem jsonSearchItem) {
        JsonLink jsonLink = jsonSearchItem.links.get(0);
        JsonInfo jsonInfo = jsonSearchItem.infoList.get(0);

        return new SearchItem(jsonLink.url, jsonInfo.title, jsonInfo.description);
    }
}
