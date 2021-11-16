package org.romanin.musicadvisor2;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PlaylistsAction extends AbstractAction {
    public PlaylistsAction(ActionConfig actionConfig){
        super(actionConfig);
    }

    @Override
    public Data execute(Data data) throws SpotifyError {
        Data returnData = new Data();
        try {
            String category = input.split(" ")[1];
            log.debug("category=" + category);
            String path = apiBase +
                    "/v1/browse/categories/" + category.toLowerCase() + "/playlists";
            log.debug("path=" + path);
            JsonObject jo = getJsonObjectFromSpotifyApi(path, accessToken, client);
            JsonArray items = jo.get("playlists").getAsJsonObject().get("items").getAsJsonArray();
            log.debug(items.toString());
            for (JsonElement item : items) {
                JsonObject obj = item.getAsJsonObject();
                String dataItem = obj.get("name").getAsString() + "\n";
                String urlSpotify = obj.get("external_urls").getAsJsonObject().get("spotify")
                        .getAsString();
                dataItem += urlSpotify + "\n";
                returnData.addItem(dataItem);
            }
        } catch (SpotifyError e) {
            log.error(e.getMessage());
            //                            e.printStackTrace();
            returnData.addItem("Unknown category name.");
        } finally {
            return returnData;
        }
    }
}
