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
    INVALID_ACCESS_TOKEN(HttpStatus.BAD_REQUEST, "MEMBER_4006", "유효하지 않은 Access Token입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "MEMBER_4007", "유효하지 않은 Refresh Token입니다."),
    MEMBER_NOT_ADMIN(HttpStatus.BAD_REQUEST, "MEMBER_4005", "권한이 없습니다."),

    // Project Error
    PROJECT_DUPLICATE(HttpStatus.BAD_REQUEST, "PROJECT_4001", "프로젝트 키가 중복됩니다."),
    PROJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "PROJECT_4002", "프로젝트가 없습니다."),
    MEMBER_NOT_IN_PROJECT(HttpStatus.NOT_FOUND, "PROJECT_4003", "프로젝트에 해당 멤버가 속하지 않습니다"),

    // File Error
    FILE_CONVERT_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_500", "파일 변환 실패"),
    FILE_NOT_EXIST(HttpStatus.NOT_FOUND, "FILE_4001", "파일이 존재하지 않습니다."),
    FILE_EXTENSION_NOT_EXIST(HttpStatus.BAD_REQUEST, "FILE_4002", "파일 확장자는 반드시 포함되어야 합니다."),
    FILE_NAME_NOT_EXIST(HttpStatus.BAD_REQUEST, "FILE_4003", "파일 이름은 반드시 포함되어야 합니다."),
    FILE_EXTENSION_NOT_IMAGE(HttpStatus.BAD_REQUEST, "FILE_4004", "이미지 파일 확장자만 업로드 가능합니다."),
    FILE_UPLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_4005", "파일 업로드 실패"),

    // ISSUE Error
    ISSUE_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "ISSUE_4001", "이슈 타입을 찾을 수 없습니다."),
    PARENT_ISSUE_NOT_FOUND(HttpStatus.NOT_FOUND, "ISSUE_4002", "부모 이슈를 찾을 수 없습니다."),
    PARENT_ISSUE_NOT_EPIC(HttpStatus.BAD_REQUEST, "ISSUE_4003", "부모 이슈는 EPIC이어야 합니다."),
    PARENT_ISSUE_NOT_STORY(HttpStatus.BAD_REQUEST, "ISSUE_4004", "부모 이슈는 STORY이어야 합니다."),
    ISSUE_NOT_FOUND(HttpStatus.NOT_FOUND, "ISSUE_4005", "이슈를 찾을 수 없습니다."),
    ISSUE_NOT_IN_PROJECT(HttpStatus.BAD_REQUEST, "ISSUE_4006", "이슈가 프로젝트에 속하지 않습니다."),
    ISSUE_TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "ISSUE_4007", "기존 이슈 타입과 일치하지 않습니다."),

    // Sprint Error
    SPRINT_NOT_FOUND(HttpStatus.NOT_FOUND, "SPRINT_4001", "스프린트를 찾을 수 없습니다."),
    SPRINT_NOT_IN_PROJECT(HttpStatus.BAD_REQUEST, "SPRINT_4002", "스프린트가 프로젝트에 속하지 않습니다."),
    INVALID_ISSUE_TYPE(HttpStatus.BAD_REQUEST, "SPRINT_4003", "이슈 타입이 EPIC은 스프린트에 할당할 수 없습니다."),

    // COMMENT Error
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT_4001", "댓글을 찾을 수 없습니다."),
    COMMENT_WRITER_MISS_MATCH(HttpStatus.BAD_REQUEST, "COMMENT_4002", "댓글 작성자가 아닙니다."),

    // Statics Error
    EPIC_STATISTIC_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "STATICS_500", "에픽 통계를 찾을 수 없습니다."),

    // Email Error
    EMAIL_NOT_SENT(HttpStatus.INTERNAL_SERVER_ERROR, "EMAIL_5001", "이메일이 정상적으로 송신되지 않았습니다."),
    INVITE_CODE_NOT_EXIST(HttpStatus.BAD_REQUEST, "EMAIL_4001", "초대 코드를 찾을 수 없습니다."),
    ALREADY_INVITATION(HttpStatus.BAD_REQUEST, "EMAIL_4002", "이미 진행 중인 초대가 있습니다"),
    EMAIL_SEND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "EMAIL_4003", "이메일 서비스에 문제가 발생했습니다. 나중에 다시 시도해주세요."),

    // Optimistic Lock Exception
    OPTIMISTIC_LOCK_EXCEPTION(HttpStatus.LOCKED, "LOCK_4000", "다른 사용자가 이미 수정했습니다. 새로 고침 후 다시 시도해주세요."),
    OPTIMISTIC_LOCK_EXCEPTION_ISSUE_NUMBER(HttpStatus.LOCKED, "LOCK_4001", "이슈 번호 생성 실패. 다시 시도해주세요.");


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
