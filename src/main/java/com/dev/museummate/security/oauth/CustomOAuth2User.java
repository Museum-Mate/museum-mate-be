package com.dev.museummate.security.oauth;

import com.dev.museummate.domain.UserRole;
import java.util.Collection;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

    private String email;
    private UserRole userRole;


    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes,
                            String nameAttributeKey, String email, UserRole userRole) {

        super(authorities, attributes, nameAttributeKey);

        this.email = email;
        this.userRole = userRole;
    }
}