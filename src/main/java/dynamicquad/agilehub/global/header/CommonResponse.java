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

    public static <T> CommonResponse<T> onSuccess(T result) {
        return new CommonResponse<>(true, SuccessStatus.OK.getCode(), SuccessStatus.OK.getMessage(), result);
    }

    public static <T> CommonResponse<T> of(BaseStatus status, T result) {
        return new CommonResponse<>(
                status.getReason().isSuccess(), status.getReason().getCode(), status.getReason().getMessage(), result);
    }
}
