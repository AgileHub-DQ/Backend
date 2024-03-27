package dynamicquad.agilehub.global.auth.controller;

import dynamicquad.agilehub.global.auth.model.JwtRefreshToken;
import dynamicquad.agilehub.global.auth.service.JwtRefreshTokenQueryService;
import dynamicquad.agilehub.global.auth.service.JwtRefreshTokenService;
import dynamicquad.agilehub.global.auth.util.JwtUtil;
import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.CommonResponse;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.global.header.status.SuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtRefreshTokenService jwtRefreshTokenService;
    private final JwtRefreshTokenQueryService jwtRefreshTokenQueryService;
    private final JwtUtil jwtUtil;

    @PostMapping("/token/refresh")
    public CommonResponse<String> refresh(@RequestHeader("Authorization") final String accessToken) {
        JwtRefreshToken refreshToken = jwtRefreshTokenQueryService.findByAccessToken(accessToken);

        if (jwtUtil.verifyToken(refreshToken.getRefreshToken())) {
            String generatedAccessToken = jwtUtil.generateAccessToken(
                    jwtUtil.extractName(refreshToken.getRefreshToken()),
                    jwtUtil.extractRole(refreshToken.getRefreshToken())
            );
            refreshToken.updateAccessToken(generatedAccessToken);

            return CommonResponse.of(SuccessStatus.CREATED, generatedAccessToken);
        }

        throw new GeneralException(ErrorStatus.INVALID_REFRESH_TOKEN);
    }

}
