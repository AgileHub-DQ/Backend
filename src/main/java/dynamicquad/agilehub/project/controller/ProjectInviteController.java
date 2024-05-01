package dynamicquad.agilehub.project.controller;

import dynamicquad.agilehub.global.auth.model.Auth;
import dynamicquad.agilehub.global.header.CommonResponse;
import dynamicquad.agilehub.member.dto.MemberRequestDto.AuthMember;
import dynamicquad.agilehub.project.controller.request.ProjectInviteRequestDto;
import dynamicquad.agilehub.project.service.ProjectInviteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.RequestToViewNameTranslator;

@Tag(name = "프로젝트 초대", description = "프로젝트에 이메일주소를 통해 초대합니다.")
@RestController
@RequiredArgsConstructor
public class ProjectInviteController {

    private final ProjectInviteService projectInviteService;
    private final RequestToViewNameTranslator viewNameTranslator;

    @Operation(summary = "프로젝트 초대", description = "이메일로 프로젝트 초대 코드를 발송합니다.")
    @PostMapping("/projects/invite")
    public CommonResponse<Void> sendInviteEmail(@Auth AuthMember authMember,
                                                @RequestBody ProjectInviteRequestDto.SendInviteMail sendInviteMail) {
        projectInviteService.sendInviteEmail(authMember, sendInviteMail);
        return CommonResponse.onSuccess(null);
    }

    @Operation(summary = "프로젝트 초대 수락", description = "초대 코드 확인 후 멤버를 프로젝트에 Editor로 추가합니다.")
    @GetMapping("/projects/invite")
    public CommonResponse<Void> receiveInviteEmail(@Auth AuthMember authMember,
                                                   @RequestParam String inviteCode) {
        projectInviteService.receiveInviteEmail(authMember, inviteCode);
        return CommonResponse.onSuccess(null);
    }

}
