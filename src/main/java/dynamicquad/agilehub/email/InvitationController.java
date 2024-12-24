package dynamicquad.agilehub.email;

import dynamicquad.agilehub.global.auth.model.Auth;
import dynamicquad.agilehub.member.dto.MemberRequestDto.AuthMember;
import dynamicquad.agilehub.project.controller.request.ProjectInviteRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/invitations")
public class InvitationController {

    private final InvitationService invitationService;

    @PostMapping
    public ResponseEntity<Void> inviteMember(@Auth AuthMember authMember,
                                             @RequestBody ProjectInviteRequestDto.SendInviteMail sendInviteMail) {
        invitationService.sendInvitation(authMember, sendInviteMail);
        return ResponseEntity.ok().build();
    }


}
