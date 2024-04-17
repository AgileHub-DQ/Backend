package dynamicquad.agilehub.global.auth.service;

import dynamicquad.agilehub.global.auth.model.JwtRefreshToken;
import dynamicquad.agilehub.global.auth.repository.RefreshTokenRedisRepository;
import dynamicquad.agilehub.global.exception.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenRedisService {

    private final RefreshTokenRedisRepository repository;

    @Transactional(readOnly = true)
    public JwtRefreshToken findByAccessToken(String accessToken) {
        return repository.findByAccessToken(accessToken)
                .orElseThrow(() -> new JwtException("Refresh Token is not exist"));
    }

    public void save(String refreshToken, String accessToken) {
        repository.save(new JwtRefreshToken(refreshToken, accessToken));
    }
}
