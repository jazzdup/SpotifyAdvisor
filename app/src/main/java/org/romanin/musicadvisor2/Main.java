package org.romanin.musicadvisor2;

import lombok.extern.slf4j.Slf4j;
import java.io.IOException;

@Slf4j
public class Main {

    public static void main(String[] args) throws IOException {
        String spotifyBase = "https://accounts.spotify.com";
        String apiBase = "https://api.spotify.com";
        int pageSize = 20; //chosen to match default page size
        int port = 8765;
        if (args.length > 1) {
            int portMin = 49152;
            int portMax = 65535;
            port = portMin + (int) (Math.random() * ((portMax - portMin) + 1));
            if ("-access".equals(args[0]))
                spotifyBase = args[1];
            if ("-resource".equals(args[2]))
                apiBase = args[3];
            if ("-page".equals(args[4]))
                pageSize = Integer.parseInt(args[5]);
        }
        MusicAdvisor musicAdvisor = new MusicAdvisor(spotifyBase, apiBase, port, pageSize);
        musicAdvisor.run();
    }
}
