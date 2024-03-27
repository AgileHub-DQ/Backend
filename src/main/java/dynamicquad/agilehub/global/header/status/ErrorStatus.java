package dynamicquad.agilehub.global.header.status;

import dynamicquad.agilehub.global.header.ReasonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseStatus {
    // Common Error
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500", "서버 에러입니다. 관리자에게 문의하세요."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_400", "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON_401", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON_403", "금지된 요청입니다."),

    // Member Error
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER_4001", "사용자가 없습니다."),
    NICKNAME_NOT_EXIST(HttpStatus.BAD_REQUEST, "MEMBER_4002", "닉네임은 필수 입니다."),
    UNSUPPORTED_PROVIDER(HttpStatus.BAD_REQUEST, "MEMBER_4003", "지원하지 않는 소셜 로그인입니다."),
    MEMBER_ROLE_NOT_EXIST(HttpStatus.BAD_REQUEST, "MEMBER_4004", "사용자 ROLE이 존재하지 않습니다."),

    // Project Error
    PROJECT_DUPLICATE(HttpStatus.BAD_REQUEST, "PROJECT_4001", "프로젝트 키가 중복됩니다."),
    PROJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "PROJECT_4002", "프로젝트가 없습니다."),

    // File Error
    FILE_CONVERT_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_500", "파일 변환 실패"),
    FILE_NOT_EXIST(HttpStatus.NOT_FOUND, "FILE_4001", "파일이 존재하지 않습니다."),
    FILE_EXTENSION_NOT_EXIST(HttpStatus.BAD_REQUEST, "FILE_4002", "파일 확장자는 반드시 포함되어야 합니다."),
    FILE_NAME_NOT_EXIST(HttpStatus.BAD_REQUEST, "FILE_4003", "파일 이름은 반드시 포함되어야 합니다."),
    FILE_EXTENSION_NOT_IMAGE(HttpStatus.BAD_REQUEST, "FILE_4004", "이미지 파일 확장자만 업로드 가능합니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDto getReason() {
        return ReasonDto.builder()
            .message(message)
            .code(code)
            .isSuccess(false)
            .build();
    }

    @Override
    public ReasonDto getReasonHttpStatus() {
        return ReasonDto.builder()
            .status(httpStatus)
            .message(message)
            .code(code)
            .isSuccess(false)
            .build();
    }
}
