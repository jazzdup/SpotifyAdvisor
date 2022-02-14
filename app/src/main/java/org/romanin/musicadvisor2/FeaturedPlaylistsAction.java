package org.romanin.musicadvisor2;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class FeaturedPlaylistsAction extends AbstractAction {
    public FeaturedPlaylistsAction(ActionConfig actionConfig){
        super(actionConfig);
    }

    @Override
    public Data execute(Data data) throws SpotifyError {
        JsonObject jo = getJsonObjectFromSpotifyApi(apiBase + "/v1/browse/featured-playlists", accessToken, client);
        JsonArray items = jo.get("playlists").getAsJsonObject().get("items").getAsJsonArray();
        Data returnData = new Data();
        for (JsonElement item : items) {
            JsonObject obj = item.getAsJsonObject();
            String dataItem = (obj.get("name").getAsString()) + " ";
            String urlSpotify = obj.get("external_urls").getAsJsonObject().get("spotify")
                    .getAsString();
            dataItem += (urlSpotify);
            returnData.addItem(dataItem);
        }
        return returnData;
    }
}
