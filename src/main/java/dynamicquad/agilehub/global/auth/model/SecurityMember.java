package dynamicquad.agilehub.global.auth.model;

import dynamicquad.agilehub.global.auth.oauth2info.OAuth2UserInfo;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Getter
public class SecurityMember implements OAuth2User {

    private final OAuth2UserInfo userInfo;

    public SecurityMember(OAuth2UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public String getName() {
        return userInfo.getNickname();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return userInfo.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "ROLE_USER");
    }

}
