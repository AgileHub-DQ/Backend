package dynamicquad.agilehub.project.service;

import dynamicquad.agilehub.global.mail.service.EmailService;
import dynamicquad.agilehub.global.util.RandomStringUtil;
import dynamicquad.agilehub.member.dto.MemberRequestDto;
import dynamicquad.agilehub.project.controller.request.ProjectInviteRequestDto;
import dynamicquad.agilehub.project.domain.InviteRedisEntity;
import dynamicquad.agilehub.project.domain.MemberProjectRole;
import dynamicquad.agilehub.project.domain.Project;
import dynamicquad.agilehub.project.model.InviteEmailInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ProjectInviteService {

    private static final String INVITE_SUBJECT = "[AgileHub] 초대코드";

    private final EmailService emailService;
    private final MemberProjectService memberProjectService;
    private final ProjectQueryService projectQueryService;
    private final InviteRedisService inviteRedisService;

    public void sendInviteEmail(MemberRequestDto.AuthMember authMember,
            ProjectInviteRequestDto.SendInviteMail sendInviteMail) {
        memberProjectService.validateMemberInProject(authMember.getId(), sendInviteMail.getProjectId());
        memberProjectService.validateMemberRole(authMember, sendInviteMail.getProjectId());

        String inviteCode = generateInviteCode();
        saveInvite(inviteCode, sendInviteMail.getProjectId());

        Project project = projectQueryService.findProjectById(sendInviteMail.getProjectId());
        InviteEmailInfo emailInfo = InviteEmailInfo.builder()
                .to(sendInviteMail.getEmail())
                .subject(INVITE_SUBJECT)
                .projectName(project.getName())
                .inviteCode(inviteCode)
                .build();

        emailService.sendMail(emailInfo, "invite")
                .thenRun(() -> log.info("이메일 전송 완료"))
                .exceptionally(e -> {
                    log.error("이메일 전송 실패", e);
                    return null;
                });
    }

    @Transactional
    public Project receiveInviteEmail(MemberRequestDto.AuthMember authMember,
            String inviteCode) {
        InviteRedisEntity inviteRedisEntity = inviteRedisService.findByInviteCode(inviteCode);
        Project project = Project.createPojoProject(Long.parseLong(inviteRedisEntity.getProjectId()));

        memberProjectService.createMemberProject(authMember, project, MemberProjectRole.EDITOR);
        return projectQueryService.findProjectById(Long.parseLong(inviteRedisEntity.getProjectId()));
    }

    private String generateInviteCode() {
        return RandomStringUtil.generateRandomKey(10);
    }

    private void saveInvite(String inviteCode, long projectId) {
        InviteRedisEntity inviteRedisEntity = InviteRedisEntity.builder()
                .inviteCode(inviteCode)
                .projectId(Long.toString(projectId))
                .build();
        inviteRedisService.save(inviteRedisEntity);
    }

}
