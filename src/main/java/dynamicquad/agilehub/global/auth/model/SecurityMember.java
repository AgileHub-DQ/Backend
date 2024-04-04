package dynamicquad.agilehub.global.auth.model;

import dynamicquad.agilehub.global.auth.oauth2info.OAuth2UserInfo;
import dynamicquad.agilehub.member.domain.Member;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Getter
public class SecurityMember implements OAuth2User {

    private final Member member;
    private OAuth2UserInfo userInfo;

    public SecurityMember(Member member, OAuth2UserInfo userInfo) {
        this.member = member;
        this.userInfo = userInfo;
    }

    public SecurityMember(Member member) {
        this.member = member;
    }

    @Override
    public String getName() {
        return userInfo.getNickname();
    }

    public String getProvider() {
        return userInfo.getProvider();
    }

    public String getId() {
        return userInfo.getId();
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
