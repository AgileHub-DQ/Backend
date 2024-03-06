package dynamicquad.agilehub.global.header;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import dynamicquad.agilehub.global.header.status.BaseStatus;
import dynamicquad.agilehub.global.header.status.SuccessStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class CommonResponse<T> {

    @JsonProperty("isSuccess")
    private final boolean isSuccess;

    private final String code;

    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T result;

    // 성공시 응답 생성
    public static <T> CommonResponse<T> onSuccess(T result) {
        return new CommonResponse<>(true, SuccessStatus.OK.getCode(), SuccessStatus.OK.getMessage(), result);
    }

    public static <T> CommonResponse<T> of(BaseStatus status, T result) {
        return new CommonResponse<>(true, status.getReason().getCode(), status.getReason().getMessage(), result);
    }

    // 실패시 응답 생성
    public static <T> CommonResponse<T> onFailure(BaseStatus status, T data) {
        return new CommonResponse<>(false, status.getReason().getCode(), status.getReason().getMessage(), data);
    }

}
