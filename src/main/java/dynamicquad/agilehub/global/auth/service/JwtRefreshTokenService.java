package dynamicquad.agilehub.global.auth.service;

import dynamicquad.agilehub.global.auth.model.JwtRefreshToken;
import dynamicquad.agilehub.global.auth.repository.JwtRepositoryTokenRepository;
import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class JwtRefreshTokenService {

    private final JwtRepositoryTokenRepository repository;

    public void saveRefreshToken(String accessToken, String refreshToken) {
        repository.save(new JwtRefreshToken(accessToken, refreshToken));
    }

    public void removeRefreshToken(String accessToken) {
        JwtRefreshToken token = repository.findByAccessToken(accessToken)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ACCESS_TOKEN_NOT_EXIST));
        repository.delete(token);
    }

}
