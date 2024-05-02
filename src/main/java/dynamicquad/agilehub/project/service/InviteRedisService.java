package dynamicquad.agilehub.project.service;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.project.domain.InviteRedisEntity;
import dynamicquad.agilehub.project.repository.InviteRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InviteRedisService {

    private final InviteRedisRepository repository;

    public InviteRedisEntity findByInviteCode(String inviteCode) {
        return repository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new GeneralException(ErrorStatus.INVITE_CODE_NOT_EXIST));
    }

    public void save(InviteRedisEntity inviteRedisEntity) {
        repository.save(inviteRedisEntity);
    }

}
