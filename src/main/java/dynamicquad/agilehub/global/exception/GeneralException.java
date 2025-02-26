package dynamicquad.agilehub.global.exception;

import dynamicquad.agilehub.global.header.ReasonDto;
import dynamicquad.agilehub.global.header.status.BaseStatus;
import lombok.Getter;

@Getter
public class GeneralException extends RuntimeException {
    private final BaseStatus status;

    public GeneralException(BaseStatus status) {
        super(status.getReason().getMessage());
        this.status = status;
    }

    public ReasonDto getErrorReason() {
        return this.status.getReason();
    }

    public ReasonDto getErrorReasonHttpStatus() {
        return this.status.getReasonHttpStatus();
    }
}
