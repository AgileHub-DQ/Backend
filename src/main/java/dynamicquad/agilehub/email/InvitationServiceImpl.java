package dynamicquad.agilehub.email;

import dynamicquad.agilehub.global.util.RandomStringUtil;
import dynamicquad.agilehub.member.dto.MemberRequestDto.AuthMember;
import dynamicquad.agilehub.project.controller.request.ProjectInviteRequestDto.SendInviteMail;
import dynamicquad.agilehub.project.service.MemberProjectService;
import dynamicquad.agilehub.project.service.ProjectQueryService;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InvitationServiceImpl implements InvitationService {

    private final MemberProjectService memberProjectService;
    private final RedisTemplate redisTemplate;
    private final SMTPService smtpService;
    private final ProjectQueryService projectQueryService;

    private static final String INVITATION_KEY_PREFIX = "invitation:";
    private static final int EXPIRATION_MINUTES = 10;

    @Override
    public void sendInvitation(AuthMember authMember, SendInviteMail sendInviteMail) {
        // 멤버 유효검사
        validateMember(authMember, sendInviteMail.getProjectId());

        // 초대 토큰 생성
        String token = generateInviteToken();
        storeInviteToken(token, sendInviteMail);

        // 이메일 전송
        sendEmail(sendInviteMail, token);
    }

    private void sendEmail(SendInviteMail sendInviteMail, String token) {
        final String projectName = projectQueryService.findProjectById(sendInviteMail.getProjectId()).getName();

        Map<String, Object> variables = new HashMap<>();
        variables.put("inviteCode", token);
        variables.put("projectName", projectName);

        smtpService.sendEmail("AgileHub 초대 메일", variables, sendInviteMail.getEmail());
    }

    private void storeInviteToken(String token, SendInviteMail sendInviteMail) {
        String key = INVITATION_KEY_PREFIX + token;
        Map<String, String> fields = new HashMap<>();
        fields.put("projectId", Long.toString(sendInviteMail.getProjectId()));
        fields.put("email", sendInviteMail.getEmail());
        fields.put("used", "false");
        fields.put("createdAt", String.valueOf(System.currentTimeMillis()));

        redisTemplate.opsForHash().putAll(key, fields);
        redisTemplate.expire(key, EXPIRATION_MINUTES, TimeUnit.MINUTES);
    }

    private void validateMember(AuthMember authMember, long projectId) {
        memberProjectService.validateMemberInProject(authMember.getId(), projectId);
        memberProjectService.validateMemberRole(authMember, projectId);
    }

    private String generateInviteToken() {
        return RandomStringUtil.generateUUID();
    }

    @Override
    public void validateInvitation(String inviteToken) {

    }
}
