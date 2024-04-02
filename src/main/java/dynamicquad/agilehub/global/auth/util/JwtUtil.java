package dynamicquad.agilehub.global.auth.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import dynamicquad.agilehub.global.auth.model.GeneratedToken;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtUtil {

    private static final String PREFIX = "Bearer ";
    private static final String BLANK = "";

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access.expiration}")
    private long accessTokenValidationSeconds;

    @Value("${jwt.refresh.expiration}")
    private long refreshTokenValidationSeconds;

    @Value("${jwt.access.header}")
    private String accessHeader;

    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    public GeneratedToken generateToken(String name, String role, String provider, String distinctId) {
        return GeneratedToken.builder()
                .accessToken(generateAccessToken(name, role, provider, distinctId))
                .refreshToken(generateRefreshToken(name, role, provider, distinctId))
                .build();
    }

    public String generateAccessToken(String name, String role, String provider, String distinctId) {
        Date now = new Date();
        return PREFIX.concat(JWT.create()
                .withIssuer("AgileHub")
                .withSubject("AccessToken")
                .withClaim("name", name)
                .withClaim("role", role)
                .withClaim("provider", provider)
                .withClaim("distinctId", distinctId)
                .withIssuedAt(now)
                .withExpiresAt(new Date(now.getTime() + accessTokenValidationSeconds * 1000))
                .sign(Algorithm.HMAC512(secretKey)));
    }

    public String generateRefreshToken(String name, String role, String provider, String distinctId) {
        Date now = new Date();
        return PREFIX.concat(JWT.create()
                .withIssuer("AgileHub")
                .withSubject("RefreshToken")
                .withClaim("name", name)
                .withClaim("role", role)
                .withClaim("provider", provider)
                .withClaim("distinctId", distinctId)
                .withIssuedAt(now)
                .withExpiresAt(new Date(now.getTime() + refreshTokenValidationSeconds * 1000))
                .sign(Algorithm.HMAC512(secretKey)));
    }

    public boolean verifyToken(String token) {
        try {
            JWT
                    .require(Algorithm.HMAC512(secretKey))
                    .build()
                    .verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(accessHeader))
                .filter(refreshToken -> refreshToken.startsWith(PREFIX))
                .map(refreshToken -> refreshToken.replace(PREFIX, BLANK));
    }

    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(refreshHeader))
                .filter(refreshToken -> refreshToken.startsWith(PREFIX))
                .map(refreshToken -> refreshToken.replace(PREFIX, BLANK));
    }

    public String extractName(String token) {
        return JWT.require(Algorithm.HMAC512(secretKey))
                .build()
                .verify(token.replace(PREFIX, BLANK))
                .getClaim("name")
                .asString();
    }

    public String extractRole(String token) {
        return JWT.require(Algorithm.HMAC512(secretKey))
                .build()
                .verify(token.replace(PREFIX, BLANK))
                .getClaim("role")
                .asString();
    }

    public String extractProvider(String token) {
        return JWT.require(Algorithm.HMAC512(secretKey))
                .build()
                .verify(token.replace(PREFIX, BLANK))
                .getClaim("provider")
                .asString();
    }

    public String extractDistinctId(String token) {
        return JWT.require(Algorithm.HMAC512(secretKey))
                .build()
                .verify(token.replace(PREFIX, BLANK))
                .getClaim("distinctId")
                .asString();
    }

}
