package org.romanin.musicadvisor2;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CategoriesAction extends AbstractAction {
    public CategoriesAction(ActionConfig actionConfig){
        super(actionConfig);
    }

    @Override
    public Data execute(Data data) throws SpotifyError {
        JsonObject jo = getJsonObjectFromSpotifyApi(apiBase + "/v1/browse/categories", accessToken, client);
        JsonArray items = jo.get("categories").getAsJsonObject().get("items").getAsJsonArray();
        Data returnData = new Data();
        for (JsonElement item : items) {
            JsonObject obj = item.getAsJsonObject();
            //            System.out.println(obj.get("name").getAsString());
            returnData.addItem(obj.get("name").getAsString());
        }
        return returnData;
    }
}

