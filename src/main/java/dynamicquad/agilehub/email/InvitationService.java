package dynamicquad.agilehub.email;

import dynamicquad.agilehub.member.dto.MemberRequestDto.AuthMember;
import dynamicquad.agilehub.project.controller.request.ProjectInviteRequestDto;

public interface InvitationService {
    void sendInvitation(AuthMember authMember, ProjectInviteRequestDto.SendInviteMail sendInviteMail);

    //초대토큰 유효한지 확인
    void validateInvitation(String inviteToken);
}

