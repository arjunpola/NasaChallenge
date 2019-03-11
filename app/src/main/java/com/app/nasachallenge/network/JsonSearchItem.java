package com.app.nasachallenge.network;


import com.app.nasachallenge.network.items.JsonInfo;
import com.app.nasachallenge.network.items.JsonLink;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JsonSearchItem {

    @SerializedName("links")
    public List<JsonLink> links;

    @SerializedName("data")
    public List<JsonInfo> infoList;
}
