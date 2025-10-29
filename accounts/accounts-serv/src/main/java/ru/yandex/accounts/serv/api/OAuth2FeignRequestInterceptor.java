package ru.yandex.accounts.serv.api;

import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import feign.RequestInterceptor;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OAuth2FeignRequestInterceptor implements RequestInterceptor {
    private final OAuth2AuthorizedClientManager authorizedClientManager;

    @Override
    public void apply(RequestTemplate template) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null) {
                log.warn("No authentication found in SecurityContext");
                return;
            }

            if (authentication instanceof OAuth2AuthenticationToken) {
                //OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;

                OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                        .withClientRegistrationId("accounts")
                        .principal(authentication)
                        .build();

                OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(authorizeRequest);

                if (authorizedClient != null && authorizedClient.getAccessToken() != null) {
                    template.header("Authorization",
                            "Bearer " + authorizedClient.getAccessToken().getTokenValue());
                    log.debug("Added OAuth2 token to Feign request for {}", template.url());
                } else {
                    log.warn("No authorized client or access token available");
                }
            } else {
                log.debug("Authentication is not OAuth2, skipping token addition");
            }

        } catch (Exception e) {
            log.error("Failed to add OAuth2 token to Feign request", e);
        }
    }
}
