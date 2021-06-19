
package io.helidon.examples.quickstart.se;

import io.helidon.common.LogConfig;
import io.helidon.common.reactive.Single;
import io.helidon.config.Config;
import io.helidon.health.HealthSupport;
import io.helidon.health.checks.HealthChecks;
import io.helidon.media.jsonp.JsonpSupport;
import io.helidon.metrics.MetricsSupport;
import io.helidon.tracing.TracerBuilder;
import io.helidon.webserver.Routing;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.staticcontent.StaticContentSupport;

import static io.helidon.config.ConfigSources.file;
import static io.helidon.config.ConfigSources.classpath;
import static io.helidon.config.PollingStrategies.regular;

import java.time.Duration;

import org.eclipse.microprofile.health.HealthCheckResponse;
/**
 * The application main class.
 */
public final class Main {

    /**
     * Cannot be instantiated.
     */
    private Main() {
    }

    /**
     * Application main entry point.
     * @param args command line arguments.
     */
    public static void main(final String[] args) {
        startServer();
    }

    /**
     * Start the server.
     * @return the created {@link WebServer} instance
     */
    static Single<WebServer> startServer() {

        // load logging configuration
        LogConfig.configureRuntime();

        // By default this will pick up application.yaml from the classpath
        Config config = buildConfig();

        WebServer server = WebServer.builder(createRouting(config))
                .config(config.get("server"))
                .tracer(TracerBuilder.create(config.get("tracing")).build())
                .addMediaSupport(JsonpSupport.create())
                .build();

        Single<WebServer> webserver = server.start();

        // Try to start the server. If successful, print some info and arrange to
        // print a message at shutdown. If unsuccessful, print the exception.
        webserver.thenAccept(ws -> {
                    System.out.println("WEB server is up! http://localhost:" + ws.port() + "/greet");
                    ws.whenShutdown().thenRun(() -> System.out.println("WEB server is DOWN. Good bye!"));
                })
                .exceptionallyAccept(t -> {
                    System.err.println("Startup failed: " + t.getMessage());
                    t.printStackTrace(System.err);
                });

        return webserver;
    }
    
    private static Config buildConfig() {

        return Config
                .builder()
                // specify config sources
                //.sources(file("/Users/pnagy/git.repos/@nagypeter/helidon-tutorial/helidon-quickstart-se/src/main/resources/se-config.properties")
                .sources(file("/Users/pnagy/git.repos/@nagypeter/helidon-tutorial-2/helidon-quickstart-se/src/main/resources/se-config.properties")
                    .pollingStrategy(regular(Duration.ofSeconds(1))),
                         classpath("application.yaml"))
                .build();
    }

    /**
     * Creates new {@link Routing}.
     *
     * @return routing configured with JSON support, a health check, and a service
     * @param config configuration of this server
     */
    @SuppressWarnings("unchecked")
	private static Routing createRouting(Config config) {

        MetricsSupport metrics = MetricsSupport.create();
        GreetService greetService = new GreetService(config);
        HealthSupport health = HealthSupport.builder()
                .addLiveness(HealthChecks.healthChecks())
                .addReadiness(() -> HealthCheckResponse.named("MySEReadinessCheck")
                        .up()
                        .withData("time", System.currentTimeMillis())
                        .build())
                .build();

        return Routing.builder()
                .register(health)                   // Health at "/health"
                .register(metrics)                  // Metrics at "/metrics"
                .register("/greet", greetService)
                .register("/", StaticContentSupport.builder("/static-content"))
                .build();
    }	
}
