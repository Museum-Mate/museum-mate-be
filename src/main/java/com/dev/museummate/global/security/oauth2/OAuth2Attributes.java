package com.dev.museummate.global.security.oauth2;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

public enum OAuth2Attributes {

    GOOGLE("google", (attributes) -> {
        return new UserProfile(
            (String) attributes.get("email"),
            (String) attributes.get("name")
        );
    }),
    NAVER("naver", (attributes) -> {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return new UserProfile(
            (String) response.get("email"),
            (String) response.get("name")
        );
    });

    private final String registrationId;
    private final Function<Map<String, Object>, UserProfile> of;

    OAuth2Attributes(String registrationId, Function<Map<String, Object>, UserProfile> of) {
        this.registrationId = registrationId;
        this.of = of;
    }

    public static UserProfile extract(String registrationId, Map<String, Object> attributes) {
        return Arrays.stream(values())
            .filter(provider -> registrationId.equals(provider.registrationId))
            .findFirst()
            .orElseThrow(IllegalArgumentException::new)
            .of.apply(attributes);
    }
}
