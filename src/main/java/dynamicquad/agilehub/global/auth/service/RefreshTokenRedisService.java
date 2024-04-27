package dynamicquad.agilehub.global.auth.service;

import dynamicquad.agilehub.global.auth.model.JwtRefreshToken;
import dynamicquad.agilehub.global.auth.repository.RefreshTokenRedisRepository;
import dynamicquad.agilehub.global.exception.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenRedisService {

    private final RefreshTokenRedisRepository repository;

    public JwtRefreshToken findByAccessToken(String accessToken) {
        return repository.findByAccessToken(accessToken)
                .orElseThrow(() -> new JwtException("Refresh Token is not exist"));
    }

    public void save(JwtRefreshToken jwtRefreshToken) {
        repository.save(jwtRefreshToken);
    }
}
