package dynamicquad.agilehub.global.exception;

import dynamicquad.agilehub.global.header.ReasonDto;
import dynamicquad.agilehub.global.header.status.BaseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {
    private final BaseStatus status;

    public ReasonDto getErrorReason() {
        return this.status.getReason();
    }

    public ReasonDto getErrorReasonHttpStatus() {
        return this.status.getReasonHttpStatus();
    }
}
