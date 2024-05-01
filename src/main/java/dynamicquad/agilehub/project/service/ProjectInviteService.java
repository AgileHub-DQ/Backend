package dynamicquad.agilehub.project.service;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.global.mail.service.EmailService;
import dynamicquad.agilehub.global.util.RandomStringUtil;
import dynamicquad.agilehub.member.dto.MemberRequestDto;
import dynamicquad.agilehub.project.controller.request.ProjectInviteRequestDto;
import dynamicquad.agilehub.project.controller.response.ProjectResponse;
import dynamicquad.agilehub.project.domain.InviteRedisEntity;
import dynamicquad.agilehub.project.model.InviteEmailInfo;
import dynamicquad.agilehub.project.repository.InviteRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProjectInviteService {

    private static final String INVITE_SUBJECT = "[AgileHub] 초대코드";

    private final EmailService emailService;
    private final MemberProjectService memberProjectService;
    private final ProjectQueryService projectQueryService;

    private final InviteRedisRepository inviteRedisRepository;

    public void sendInviteEmail(MemberRequestDto.AuthMember authMember,
                                ProjectInviteRequestDto.SendInviteMail sendInviteMail) {
        memberProjectService.validateMemberInProject(authMember.getId(), sendInviteMail.getProjectId());
        memberProjectService.validateMemberRole(authMember, sendInviteMail.getProjectId());

        String inviteCode = generateInviteCode();
        saveInvite(inviteCode, sendInviteMail.getProjectId());

        ProjectResponse projectDto = projectQueryService.findProjectById(sendInviteMail.getProjectId());
        InviteEmailInfo emailInfo = InviteEmailInfo.builder()
                .to(sendInviteMail.getEmail())
                .subject(INVITE_SUBJECT)
                .projectName(projectDto.getName())
                .inviteCode(inviteCode)
                .build();

        emailService.sendMail(emailInfo, "invite");
    }

    public void receiveInviteEmail(MemberRequestDto.AuthMember authMember,
                                   String inviteCode) {

    }

    private String generateInviteCode() {
        return RandomStringUtil.generateRandomKey(10);
    }

    private void saveInvite(String inviteCode, long projectId) {
        InviteRedisEntity inviteRedisEntity = InviteRedisEntity.builder()
                .inviteCode(inviteCode)
                .projectId(Long.toString(projectId))
                .build();
        inviteRedisRepository.save(inviteRedisEntity);
    }

    private void validateInviteCode(String inviteCode) {
        if (inviteRedisRepository.findByInviteCode(inviteCode).isEmpty()) {
            throw new GeneralException(ErrorStatus.INVITE_CODE_NOT_EXIST);
        }
    }

}
