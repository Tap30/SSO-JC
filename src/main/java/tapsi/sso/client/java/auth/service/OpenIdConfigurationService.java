package tapsi.sso.client.java.auth.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tapsi.sso.client.java.auth.model.OidcConfiguration;

import java.time.Duration;

@Service
@Slf4j
public class OpenIdConfigurationService {
    private final OpenIdConfigurationCache openIdConfigurationCache = new OpenIdConfigurationCache();
    private final WebClient webClient;
    private final String issuerUri;
    private final String baseUrl;

    public OpenIdConfigurationService(WebClient webClient,
                                      @Value("${sso.issuer-uri}") String issuerUri,
                                      @Value("${api.base-url}") String baseUrl) {
        this.webClient = webClient;
        this.issuerUri = issuerUri;
        this.baseUrl = baseUrl;
    }

    public Mono<OidcConfiguration> getConfiguration() {
        return openIdConfigurationCache.get()
                .doOnNext(
                        configs -> {
                            configs.setTokenEndpoint(this.baseUrl + "/token");
                            configs.setUserinfoEndpoint(this.baseUrl + "/userinfo");
                            configs.setEndSessionEndpoint(this.baseUrl + "/logout");
                        }
                );
    }

    public Mono<String> getTokenEndpoint() {
        return openIdConfigurationCache.get().map(OidcConfiguration::getTokenEndpoint);
    }
    public Mono<String> getUserInfoEndpoint() {
        return openIdConfigurationCache.get().map(OidcConfiguration::getUserinfoEndpoint);
    }
    public Mono<String> getLogoutEndpoint() {
        return openIdConfigurationCache.get().map(OidcConfiguration::getEndSessionEndpoint);
    }

    private class OpenIdConfigurationCache {
        private static final String CACHE_KEY = "conf";
        private static final Cache<String, OidcConfiguration> CACHE = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(10))
                .build();

        public Mono<OidcConfiguration> get() {
            return Mono.justOrEmpty(CACHE.getIfPresent(CACHE_KEY)).switchIfEmpty(sync());
        }

        // todo
        private Mono<OidcConfiguration> sync() {
            return webClient.get()
                    .uri(issuerUri + "/.well-known/openid-configuration")
                    .retrieve()
                    .bodyToMono(OidcConfiguration.class)
                    .doOnNext(oidcConfiguration -> {
                        log.info("retrieved latest oidc configuration from ss :{}", oidcConfiguration);
                        CACHE.put(CACHE_KEY, oidcConfiguration);
                    });
        }
    }
}
