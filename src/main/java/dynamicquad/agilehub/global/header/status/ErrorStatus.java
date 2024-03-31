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
    ISSUE_TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "ISSUE_4007", "기존 이슈 타입과 일치하지 않습니다.");

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
