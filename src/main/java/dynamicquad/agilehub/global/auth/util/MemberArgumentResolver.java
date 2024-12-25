package dynamicquad.agilehub.global.auth.util;

import dynamicquad.agilehub.global.auth.model.Auth;
import dynamicquad.agilehub.global.auth.model.SecurityMember;
import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.member.dto.MemberRequestDto.AuthMember;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;


public class MemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final String activeProfile;

    public MemberArgumentResolver(String activeProfile) {
        this.activeProfile = activeProfile;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Auth.class) && parameter.getParameterType().equals(AuthMember.class);
    }


    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        if (activeProfile.equals("prod")) {
            return resolveProductionAuthMember();
        }

        return resolveTestAuthMember();
    }

    private Object resolveTestAuthMember() {
        return AuthMember.builder()
            .id(1L)
            .name("User1")
            .profileImageUrl("adada")
            .build();
    }

    private Object resolveProductionAuthMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        SecurityMember principal = (SecurityMember) authentication.getPrincipal();
        Member member = principal.getMember();

        return AuthMember.builder()
            .id(member.getId())
            .name(member.getName())
            .profileImageUrl(member.getProfileImageUrl())
            .build();
    }
}
