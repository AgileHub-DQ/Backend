package dynamicquad.agilehub.global.header.status;

import dynamicquad.agilehub.global.header.ReasonDto;

public interface BaseStatus {
    public ReasonDto getReason();

    public ReasonDto getReasonHttpStatus();
}
