package dynamicquad.agilehub.project.controller;

import dynamicquad.agilehub.global.auth.model.Auth;
import dynamicquad.agilehub.global.header.CommonResponse;
import dynamicquad.agilehub.member.dto.MemberRequestDto.AuthMember;
import dynamicquad.agilehub.project.controller.request.ProjectInviteRequestDto;
import dynamicquad.agilehub.project.controller.response.ProjectResponseDto;
import dynamicquad.agilehub.project.domain.Project;
import dynamicquad.agilehub.project.service.ProjectInviteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "프로젝트 초대", description = "프로젝트에 이메일주소를 통해 초대합니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/projects/invite")
public class ProjectInviteController {

    private final ProjectInviteService projectInviteService;

    @Operation(summary = "프로젝트 초대", description = "이메일로 프로젝트 초대 코드를 발송합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "EMAIL_5001", description = "이메일이 정상적으로 송신되지 않았습니다.")
    })
    @PostMapping("/send")
    public CommonResponse<Void> sendInviteEmail(@Auth AuthMember authMember,
                                                @RequestBody ProjectInviteRequestDto.SendInviteMail sendInviteMail) {
        projectInviteService.sendInviteEmail(authMember, sendInviteMail);
        return CommonResponse.onSuccess(null);
    }

    @Operation(summary = "프로젝트 초대 수락", description = "초대 코드 확인 후 멤버를 프로젝트에 Editor로 추가합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "EMAIL_4001", description = "초대 코드를 찾을 수 없습니다.")
    })
    @PostMapping("/receive")
    public CommonResponse<ProjectResponseDto> receiveInviteEmail(
            @Auth AuthMember authMember,
            @RequestBody @Valid ProjectInviteRequestDto.ReceiveInviteMail receiveInviteMail) {
        Project project = projectInviteService.receiveInviteEmail(authMember, receiveInviteMail.getInviteCode());
        return CommonResponse.onSuccess(ProjectResponseDto.fromEntity(project));
    }

}
