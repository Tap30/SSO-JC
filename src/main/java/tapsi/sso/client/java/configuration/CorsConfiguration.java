package tapsi.sso.client.java.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static tapsi.sso.client.java.util.ErrorProneFunctionalInterfaceExecutor.getOrDefault;

@Configuration
public class CorsConfiguration {

    private final Set<String> corsAllowedOrigins;
    private final String corsAllowedMethods;
    private final String corsAllowedHeaders;
    private final boolean corsAllowCredentials;

    public CorsConfiguration(@Value("${cors.allowed-origins}")
                             String[] corsAllowedOrigins,
                             @Value("${cors.allowed-methods}")
                             String corsAllowedMethods,
                             @Value("${cors.allowed-headers}")
                             String corsAllowedHeaders,
                             @Value("${cors.allow-credentials}")
                             boolean corsAllowCredentials) {
        this.corsAllowedOrigins = new HashSet<>(Arrays.asList(corsAllowedOrigins));
        this.corsAllowedMethods = corsAllowedMethods;
        this.corsAllowedHeaders = corsAllowedHeaders;
        this.corsAllowCredentials = corsAllowCredentials;
    }

    @Bean
    public WebFilter corsFilter() {
        return (ctx, chain) -> {
            var request = ctx.getRequest();
            if (CorsUtils.isCorsRequest(request)) {
                var response = ctx.getResponse();
                var headers = response.getHeaders();
                headers.add("Access-Control-Allow-Origin", this.getAllowedOrigin(request));
                headers.add("Access-Control-Allow-Methods", this.corsAllowedMethods);
                headers.add("Access-Control-Max-Age", "1800");
                headers.add("Access-Control-Allow-Headers", this.corsAllowedHeaders);
                headers.add("Access-Control-Allow-Credentials", Boolean.toString(this.corsAllowCredentials));
                if (request.getMethod() == HttpMethod.OPTIONS) {
                    response.setStatusCode(HttpStatus.OK);
                    return Mono.empty();
                }
            }
            return chain.filter(ctx);
        };
    }

    String getAllowedOrigin(ServerHttpRequest request) {
        var origin = request.getHeaders().getOrigin() != null
                ? request.getHeaders().getOrigin()
                : "";
        var originHost = getOrDefault(() -> new URL(origin).getHost(), origin);
        if (this.corsAllowedOrigins.contains("*") || this.corsAllowedOrigins.stream().anyMatch(originHost::endsWith)) {
            return origin;
        } else {
            return "";
        }
    }

}
