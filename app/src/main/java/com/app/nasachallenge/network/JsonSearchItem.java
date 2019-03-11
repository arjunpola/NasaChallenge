package com.app.nasachallenge.network;


import com.app.nasachallenge.network.items.JsonInfo;
import com.app.nasachallenge.network.items.JsonLink;
import com.google.gson.annotations.SerializedName;

import java.util.List;

class JsonSearchItem {

    @SerializedName("links")
    List<JsonLink> links;

    @SerializedName("data")
    List<JsonInfo> infoList;
}
