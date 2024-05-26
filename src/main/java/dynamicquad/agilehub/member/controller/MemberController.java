package dynamicquad.agilehub.member.controller;

import dynamicquad.agilehub.global.auth.model.Auth;
import dynamicquad.agilehub.global.header.CommonResponse;
import dynamicquad.agilehub.member.dto.MemberRequestDto.AuthMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "멤버", description = "멤버를 조회합니다")
@RestController
public class MemberController {

    @Operation(summary = "멤버 조회", description = "멤버를 조회합니다")
    @GetMapping("/member/profile")
    public CommonResponse<?> getMember(@Auth AuthMember authMember) {

        return CommonResponse.onSuccess(authMember);
    }
}
