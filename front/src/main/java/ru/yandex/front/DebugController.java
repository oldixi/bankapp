package ru.yandex.front;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DebugController {

    @Autowired(required = false)
    private OAuth2AuthorizedClientManager clientManager;

    @GetMapping("/debug/token")
    public String debugToken() {
        try {
            if (clientManager == null) {
                return "OAuth2AuthorizedClientManager is NULL";
            }

            OAuth2AuthorizeRequest request = OAuth2AuthorizeRequest.withClientRegistrationId("keycloak")
                    .principal("front")
                    .build();

            OAuth2AuthorizedClient client = clientManager.authorize(request);

            if (client == null) {
                return "OAuth2AuthorizedClient is NULL - cannot get token";
            }

            OAuth2AccessToken token = client.getAccessToken();
            return "Token: " + token.getTokenValue().substring(0, 50) + "...";

        } catch (Exception e) {
            return "Token ERROR: " + e.getClass().getSimpleName() + " - " + e.getMessage();
        }
    }
}
