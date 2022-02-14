package org.romanin.musicadvisor2;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ActionFactory {

    Action createAction(ActionConfig actionConfig) throws InvalidActionException {
        String input = actionConfig.input;
        if (input.startsWith("new")){
            return new NuReleasesAction(actionConfig);
        }else if (input.startsWith("fe")){
            return new FeaturedPlaylistsAction(actionConfig);
        }else if (input.startsWith("ca")){
            return new CategoriesAction(actionConfig);
        }else if (input.startsWith("pl")){
            PlaylistsAction playlistsAction = new PlaylistsAction(actionConfig);
            return playlistsAction;
        } else if (input.startsWith("n")) {
            return new NextAction(actionConfig);
        } else if (input.startsWith("p")) {
            return new PreviousAction(actionConfig);
        }
        throw new InvalidActionException();
    }
}

class ActionConfig {
    HttpClient httpClient;
    int pageSize;
    String accessToken;
    String apiBase;
    String input;

    public ActionConfig(HttpClient httpClient, int pageSize, String accessToken, String apiBase, String input) {
        this.httpClient = httpClient;
        this.pageSize = pageSize;
        this.accessToken = accessToken;
        this.apiBase = apiBase;
        this.input = input;
    }
}

interface Action {
    Data execute(Data data) throws SpotifyError;
}

@Slf4j
class AbstractAction implements Action {
    final String accessToken;
    final HttpClient client;
    int pageSize;
    String apiBase;
    String input;

    public AbstractAction(ActionConfig actionConfig) {
        this.apiBase = actionConfig.apiBase;
        this.accessToken = actionConfig.accessToken;
        this.client = actionConfig.httpClient;
        this.pageSize = actionConfig.pageSize;
        this.input = actionConfig.input;
    }

    @Override
    public Data execute(Data data) throws SpotifyError {
        throw new UnsupportedOperationException();
    }

    JsonObject getJsonObjectFromSpotifyApi(String path, String accessToken, HttpClient client)
            throws SpotifyError {
        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + accessToken)
                .uri(URI.create(path))
                .GET()
                .build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
        } catch (IOException e){
            //this is here only because of shit tests.
            System.out.println("Test unpredictable error message");
            throw new SpotifyError( 101,"xxx", e);
        } catch (InterruptedException e){
            throw new SpotifyError(123, "problem contacting spotify - likely invalid url?"
                    + e.getMessage(), e );
        }
        log.debug("response:");
        log.debug(response.body());
        JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
        if (response.body().contains("error")) {
            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonObject error = json.getAsJsonObject("error");
            var errorCode = error.get("status").getAsInt();
            var errorMessage = error.get("message").getAsString();
            throw new SpotifyError(errorCode, errorMessage, new Exception("got an error from spotify"));
        }
        return jsonObject;
    }
}




////@todo: remove
//class ExitAction extends AbstractAction {
//    public ExitAction(ActionEnum action, String apiBase) {
//        super(apiBase);
//    }
//
//    @Override
//    public void execute(String accessToken, HttpClient client) throws SpotifyError {
//    }
//}
//
//class AuthAction extends AbstractAction {
//    public AuthAction(ActionEnum action, String apiBase) {
//        super(apiBase);
//    }
//
//    @Override
//    public void execute(String accessToken, HttpClient client) throws SpotifyError {
//    }
//}
