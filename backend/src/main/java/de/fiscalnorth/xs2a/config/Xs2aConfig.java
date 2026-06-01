package de.fiscalnorth.xs2a.config;

import io.finapi.xs2a.ApiClient;
import io.finapi.xs2a.api.AccountInformationServiceAisApi;
import io.finapi.xs2a.api.TransactionHistoryApiApi;
import io.finapi.xs2a.auth.OAuth;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Xs2aConfig {

    @Bean
    @ConditionalOnProperty(name = "app.xs2a.enabled", havingValue = "true")
    public ApiClient xs2aApiClient(Xs2aProperties properties) {
        ApiClient client = new ApiClient();
        client.setBasePath(properties.getBaseUrl().replaceAll("/$", ""));
        OAuth auth = (OAuth) client.getAuthentication("ApiOAuth2");
        if (auth != null) {
            auth.setAccessToken(properties.getAccessToken());
        }
        return client;
    }

    @Bean
    @ConditionalOnProperty(name = "app.xs2a.enabled", havingValue = "true")
    public AccountInformationServiceAisApi accountInformationServiceAisApi(ApiClient xs2aApiClient) {
        return new AccountInformationServiceAisApi(xs2aApiClient);
    }

    @Bean
    @ConditionalOnProperty(name = "app.xs2a.enabled", havingValue = "true")
    public TransactionHistoryApiApi transactionHistoryApiApi(ApiClient xs2aApiClient) {
        return new TransactionHistoryApiApi(xs2aApiClient);
    }
}
