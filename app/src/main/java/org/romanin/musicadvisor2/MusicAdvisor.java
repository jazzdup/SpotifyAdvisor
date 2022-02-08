package org.romanin.musicadvisor2;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.Scanner;

@Slf4j
public class MusicAdvisor {
    static final String REDIRECT_URI = "http://localhost";
    static final String SPOTIFY_PATH = "/api/token";
    private final String CLIENT_ID = "1e3e3fc2ce72464aa85ec9c082ef812c";
    //@todo: NEVER STORE SECRETS IN CODE!!!
    private final String CLIENT_SECRET = "e7666986313d4b8cb7725255e744eeb3";
    private final String apiBase;

    private String serverBase;
    private int redirectPort;
    private int pageSize;
    private  String tokenPath;

    private ActionFactory actionFactory;
    private HttpServer server;
    private HttpClient client;

    private String code;
    private String accessToken;
    private String refreshToken;

    private boolean exit = false;
    private boolean auth = false;

    public MusicAdvisor(String serverBase, String apiBase, int redirectPort, int pageSize) throws IOException {
        log.warn("started MusicAdvisor");
        this.serverBase = serverBase;
        this.redirectPort = redirectPort;
        this.tokenPath = serverBase + SPOTIFY_PATH;
        this.actionFactory = new ActionFactory();
        this.apiBase = apiBase;

        this.pageSize = pageSize;
        this.server = HttpServer.create();
        this.server.bind(new InetSocketAddress(this.redirectPort), 0);
        this.server.createContext("/", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                authenticate(exchange, server);
            }
        });
        this.client = HttpClient.newBuilder().build();
//        final ExecutorService ex = Executors.newSingleThreadExecutor();
//        final CountDownLatch c = new CountDownLatch(1);
//        this.server.setExecutor(ex);
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        Data data = new Data();
        while(! exit) {
            try {
                String input = scanner.nextLine();
                boolean doingAuth = checkAuth(input);
                if (! doingAuth) {
                    ActionConfig actionConfig = new ActionConfig(client, pageSize, accessToken,
                            apiBase, input);
                    Action action = actionFactory.createAction(actionConfig);
                    data = action.execute(data);
                    data.print(pageSize);
                }
            } catch (SpotifyError e) {
                System.out.println(e.getMessage());
            } catch (InvalidActionException e) {
                System.out.println("---INVALID INPUT!---");
            }
        }
    }

    private void doExit() {
        exit = true;
        log.debug("stopping server...");
        server.stop(0);
        System.exit(0);
    }

    private void startAuthFlow() {
        try {
            System.out.println("use this link to request the access code:\n");
            System.out.println(serverBase + "/authorize?client_id=" + CLIENT_ID +
                    "&redirect_uri="
                    + REDIRECT_URI + ":" + redirectPort + "&response_type=code \n");
            server.start();
            while (code == null) {
                System.out.println("waiting for code...");
                Thread.sleep(2000);
            }
            log.debug("got code");
            System.out.println("NEW albums | FEatured playlists | CAtegories(all) | PLaylists in category | \n" +
                    "Next | Previous | EXIT");
            return;
        } catch (InterruptedException e) {
            // Restore interrupted state...
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    private boolean checkAuth(String input){
        if (input.startsWith("auth")) {
            startAuthFlow();
            return true;
        } else if (input.startsWith("exit")) {
            doExit();
        } else if (!auth) {
            System.out.println("Please, provide access for application.");
            return true;
        }
        return false;
    }

    private String getCategory(String input) {
        if (input.split(" ").length > 1) {
            int spaceIndex = input.indexOf(" ") + 1;
            return input.substring(spaceIndex);
        } else return "";
    }

    private void authenticate(HttpExchange exchange, HttpServer server) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        log.debug(query);
        if (query == null) {
            sendMessageToBrowser(exchange, 400, "Authorization code not found. Try again." );
            return;
        }
        String[] codeKV = query.split("=");
        if ("code".equals(codeKV[0])){
            this.code = codeKV[1];
            log.debug("before sending response");
            sendMessageToBrowser(exchange, 200, "Got the code. Return back to your program." );
            System.out.println("code received");
            requestAccessToken();
        }else{
            sendMessageToBrowser(exchange, 400, "Authorization code not found. Try again." );
        }
    }

    private void requestAccessToken() throws IOException {
        String clientIdSecret = CLIENT_ID + ":" + CLIENT_SECRET;
        String authHeader = "Basic " + Base64.getEncoder().encodeToString(clientIdSecret.getBytes());
        log.debug("Using... Authorization: " + authHeader);
            HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", authHeader)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .uri(URI.create(tokenPath))
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=authorization_code" +
                        "&code=" + code +
                        "&redirect_uri=" + REDIRECT_URI + ":" + redirectPort))
                .build();
            System.out.println("making http request for access_token...");
        HttpResponse<String> response = null;
        try {
            response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw e;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
        log.debug("response:");
            log.debug(response.body());
            System.out.println("Success!");
            JsonObject jo = JsonParser.parseString(response.body()).getAsJsonObject();
            accessToken = jo.get("access_token").getAsString();
            refreshToken = jo.get("refresh_token").getAsString();
            auth = true;
            log.info(accessToken);
            log.info(refreshToken);

    }

    private void sendMessageToBrowser(HttpExchange exchange, int responseCode, String message) throws IOException {
        exchange.sendResponseHeaders(responseCode, message.length());
        exchange.getResponseBody().write(message.getBytes());
        exchange.getResponseBody().close();
    }
}
