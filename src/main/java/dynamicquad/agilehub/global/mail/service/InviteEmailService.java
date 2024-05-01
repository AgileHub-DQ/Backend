package dynamicquad.agilehub.global.mail.service;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.global.mail.dto.MailRequestDto;
import dynamicquad.agilehub.global.mail.model.InviteEmailInfo;
import dynamicquad.agilehub.global.mail.model.InviteRedisEntity;
import dynamicquad.agilehub.global.mail.repository.InviteCodeRedisRepository;
import dynamicquad.agilehub.global.util.RandomStringUtil;
import dynamicquad.agilehub.member.dto.MemberRequestDto;
import dynamicquad.agilehub.project.controller.response.ProjectResponse;
import dynamicquad.agilehub.project.service.MemberProjectService;
import dynamicquad.agilehub.project.service.ProjectQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class InviteEmailService {

    private static final String INVITE_SUBJECT = "[AgileHub] 초대코드";

    private final EmailService emailService;
    private final MemberProjectService memberProjectService;
    private final ProjectQueryService projectQueryService;

    private final InviteCodeRedisRepository inviteCodeRedisRepository;

    public void sendInviteEmail(MemberRequestDto.AuthMember authMember,
                                MailRequestDto.SendInviteMail sendInviteMail) {
        memberProjectService.validateMemberInProject(authMember.getId(), sendInviteMail.getProjectId());
        memberProjectService.validateMemberRole(authMember, sendInviteMail.getProjectId());

        String inviteCode = generateInviteCode();
        ProjectResponse projectDto = projectQueryService.findProjectById(sendInviteMail.getProjectId());

        InviteEmailInfo emailInfo = InviteEmailInfo.builder()
                .to(sendInviteMail.getEmail())
                .subject(INVITE_SUBJECT)
                .projectName(projectDto.getName())
                .inviteCode(inviteCode)
                .build();

        emailService.sendMail(emailInfo, "invite");
    }

    public void validateInviteCode(String inviteCode) {
        if (inviteCodeRedisRepository.findByInviteCode(inviteCode).isEmpty()) {
            throw new GeneralException(ErrorStatus.INVITE_CODE_NOT_EXIST);
        }
    }

    private String generateInviteCode() {
        String randomKey = RandomStringUtil.generateRandomKey(10);

        InviteRedisEntity inviteRedisEntity = InviteRedisEntity.builder()
                .inviteCode(randomKey)
                .build();
        inviteCodeRedisRepository.save(inviteRedisEntity);

        return randomKey;
    }

}
