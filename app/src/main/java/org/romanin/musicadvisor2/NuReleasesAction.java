package org.romanin.musicadvisor2;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class NuReleasesAction extends AbstractAction {
    public NuReleasesAction(ActionConfig actionConfig){
        super(actionConfig);
    }

    @Override
    public Data execute(Data data) throws SpotifyError {
        JsonObject jo = getJsonObjectFromSpotifyApi(apiBase + "/v1/browse/new-releases", accessToken, client);
        JsonArray items = jo.get("albums").getAsJsonObject().get("items").getAsJsonArray();
        Data returnData = new Data();
        for (JsonElement item : items) {
            JsonObject obj = item.getAsJsonObject();
            String dataItem = obj.get("name").getAsString() + " ";
            JsonArray artists = obj.getAsJsonArray("artists");
            List arts = new ArrayList<String>();
            for (JsonElement a : artists) {
                JsonObject art = a.getAsJsonObject();
                arts.add(art.get("name").getAsString());
            }
            dataItem += arts.toString() + " ";
            String urlSpotify = obj.get("external_urls").getAsJsonObject().get("spotify")
                    .getAsString();
            dataItem += urlSpotify;
            returnData.addItem(dataItem);
        }
        return returnData;
    }
}