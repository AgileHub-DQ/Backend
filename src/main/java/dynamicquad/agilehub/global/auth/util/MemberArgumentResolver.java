package dynamicquad.agilehub.global.auth.util;

import dynamicquad.agilehub.global.auth.model.Auth;
import dynamicquad.agilehub.member.dto.MemberRequestDto.AuthMember;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class MemberArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Auth.class) && parameter.getParameterType().equals(AuthMember.class);
    }

//    @Override
//    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
//                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        SecurityMember principal = (SecurityMember) authentication.getPrincipal();
//        Member member = principal.getMember();
//
//        return AuthMember.builder()
//            .id(member.getId())
//            .name(member.getName())
//            .profileImageUrl(member.getProfileImageUrl())
//            .build();
//    }


    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        // 테스트용 유저1 -> 모든 API에 해당 유저만 진입됨 -> API 진입 유저 1 이 여러번 생성과 수정과 조회 하는 것도 의미있다고 봅니다
        return AuthMember.builder()
            .id(1L)
            .name("User1")
            .profileImageUrl("adada")
            .build();
    }
}
