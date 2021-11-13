package advisor;

import java.io.IOException;

/**
 * //    -Djava.util.logging.config.file="logging.properties"
 * //    .level= INFO
 * //    com.xyz.foo.level = SEVERE
 * //     C:\Program Files\Java\jre1.8.0_221\lib\logging.properties
 */
public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        String spotifyBase = "https://accounts.spotify.com";
        String apiBase = "https://api.spotify.com";
        int pageSize = 5;
        int port = 8765;
        if (args.length > 1) {
            int portMin = 49152;
            int portMax = 65535;
            port = portMin + (int) (Math.random() * ((portMax - portMin) + 1));
            if( "-access".equals(args[0]))
                spotifyBase = args[1];
            if( "-resource".equals(args[2]))
                apiBase = args[3];
            if( "-page".equals(args[4]))
                pageSize = Integer.parseInt(args[5]);
        }
        MusicAdvisor musicAdvisor = new MusicAdvisor(spotifyBase, apiBase, port, pageSize);
        musicAdvisor.run();

    }
}
