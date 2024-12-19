package dynamicquad.agilehub.email;

import dynamicquad.agilehub.member.dto.MemberRequestDto.AuthMember;
import dynamicquad.agilehub.project.controller.request.ProjectInviteRequestDto.SendInviteMail;
import dynamicquad.agilehub.project.service.MemberProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InvitationServiceImpl implements InvitationService {

    private final MemberProjectService memberProjectService;

    @Override
    public void sendInvitation(AuthMember authMember, SendInviteMail sendInviteMail) {
        // 멤버 유효검사
        memberProjectService.validateMemberInProject(authMember.getId(), sendInviteMail.getProjectId());
        memberProjectService.validateMemberRole(authMember, sendInviteMail.getProjectId());

        // 초대 토큰 생성
        String token = generateInviteToken();
    }

    private String generateInviteToken() {
        return null;
    }

    @Override
    public void validateInvitation(String inviteToken) {

    }
}
