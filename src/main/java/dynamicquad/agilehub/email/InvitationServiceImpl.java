package dynamicquad.agilehub.email;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.global.util.RandomStringUtil;
import dynamicquad.agilehub.member.dto.MemberRequestDto.AuthMember;
import dynamicquad.agilehub.project.controller.request.ProjectInviteRequestDto.SendInviteMail;
import dynamicquad.agilehub.project.service.MemberProjectService;
import dynamicquad.agilehub.project.service.ProjectQueryService;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InvitationServiceImpl implements InvitationService {

    private final MemberProjectService memberProjectService;
    private final RedisTemplate<String, String> redisTemplate;
    private final SMTPService smtpService;
    private final ProjectQueryService projectQueryService;

    // 초대 코드 저장 key prefix
    private static final String KEY_PREFIX = "i:";

    // 초대 코드 상태 저장 key prefix
    private static final String STATUS_PREFIX = "status:";

    // 초대 코드 만료 시간
    private static final int EXPIRATION_MINUTES = 10;

    @Override
    public void sendInvitation(AuthMember authMember, SendInviteMail sendInviteMail) {
        validateMember(authMember, sendInviteMail.getProjectId());
        // 진행 중인 초대가 있는 지 확인
        String email = validateMail(sendInviteMail);

        String token = generateInviteToken();
        storeInviteToken(token, sendInviteMail);
        storeInvitationStatus(email, InvitationStatus.PENDING);

        sendEmail(sendInviteMail, token);
    }

    private String validateMail(SendInviteMail sendInviteMail) {
        String email = sendInviteMail.getEmail();
        if (hasActiveInvitation(email)) {
            throw new GeneralException(ErrorStatus.ALREADY_INVITATION);
        }
        else if (isEmailServiceDown(email)) {
            // 이메일 서비스가 고장나 있을 때
            throw new GeneralException(ErrorStatus.EMAIL_SEND_FAIL);
        }
        return email;
    }

    private boolean isEmailServiceDown(String email) {
        String statusKey = STATUS_PREFIX + email;
        String status = (String) redisTemplate.opsForValue().get(statusKey);
        return status != null && (InvitationStatus.isFailed(status));
    }

    private void storeInvitationStatus(String email, InvitationStatus invitationStatus) {
        String statusKey = STATUS_PREFIX + email;
        // 10분 동안 이메일 상태 유지
        redisTemplate.opsForValue().set(statusKey, invitationStatus.name(), EXPIRATION_MINUTES, TimeUnit.MINUTES);
    }

    private boolean hasActiveInvitation(String email) {
        String statusKey = STATUS_PREFIX + email;
        String status = redisTemplate.opsForValue().get(statusKey);
        return status != null && (InvitationStatus.isPendingOrSending(status));
    }

    private void storeInviteToken(String token, SendInviteMail sendInviteMail) {
        String tokenBase64 = RandomStringUtil.uuidToBase64(token);
        String key = KEY_PREFIX + tokenBase64;

        Map<String, Object> fields = new HashMap<>();
        fields.put("p", String.valueOf(sendInviteMail.getProjectId())); // projectId -> p
        fields.put("u", "0");  // used 상태

        redisTemplate.opsForHash().putAll(key, fields);
        redisTemplate.expire(key, EXPIRATION_MINUTES, TimeUnit.MINUTES);
    }


    private void sendEmail(SendInviteMail sendInviteMail, String token) {
        final String projectName = projectQueryService.findProjectById(sendInviteMail.getProjectId()).getName();

        Map<String, Object> variables = new HashMap<>();
        variables.put("inviteCode", token);
        variables.put("projectName", projectName);

        smtpService.sendEmail("AgileHub 초대 메일", variables, sendInviteMail.getEmail())
            .thenRun(() -> {
                storeInvitationStatus(sendInviteMail.getEmail(), InvitationStatus.SENT);
                log.info("이메일 전송 완료");
            })
            .exceptionally(e -> {
                log.error("이메일 전송 실패", e);
                storeInvitationStatus(sendInviteMail.getEmail(), InvitationStatus.FAILED);
                return null;
            });
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