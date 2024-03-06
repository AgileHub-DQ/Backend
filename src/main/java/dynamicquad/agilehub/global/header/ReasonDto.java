package dynamicquad.agilehub.global.header;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class ReasonDto {
    private HttpStatus status;

    private boolean isSuccess;

    private String code;

    private String message;

    public boolean isSuccess() {
        return isSuccess;
    }
}
