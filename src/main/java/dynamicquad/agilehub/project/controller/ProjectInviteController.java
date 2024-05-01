package dynamicquad.agilehub.project.controller;

import dynamicquad.agilehub.global.auth.model.Auth;
import dynamicquad.agilehub.member.dto.MemberRequestDto.AuthMember;
import dynamicquad.agilehub.project.controller.request.ProjectInviteRequestDto;
import dynamicquad.agilehub.project.service.ProjectInviteService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProjectInviteController {

    private final ProjectInviteService projectInviteService;

    @Operation(summary = "프로젝트 초대", description = "이메일로 프로젝트 초대 코드를 발송합니다.")
    @PostMapping("/projects/invite")
    public ResponseEntity<Void> sendInviteEmail(@Auth AuthMember authMember,
                                                @RequestBody ProjectInviteRequestDto.SendInviteMail sendInviteMail) {
        projectInviteService.sendInviteEmail(authMember, sendInviteMail);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
