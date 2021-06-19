package io.helidon.examples.quickstart.mp;

import io.helidon.config.Config;
import io.helidon.microprofile.server.Server;
import java.io.IOException;
import static io.helidon.config.ConfigSources.classpath;

public final class Main {

    private Main() { }

    public static void main(final String[] args) throws IOException {
        Server server = startServer();
        System.out.println("http://localhost:" + server.port() + "/greet");
    }

    static Server startServer() {
        return Server.builder()
        	.config(buildConfig())
            .build()
            .start();
    }
    
    private static Config buildConfig() {
        return Config.builder()
            .disableEnvironmentVariablesSource() 
            .sources(
                classpath("mp-config.yaml").optional(), 
                classpath("META-INF/microprofile-config.properties")) 
            .build();
     }
}
