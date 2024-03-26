package dynamicquad.agilehub.global.exception;

import dynamicquad.agilehub.global.header.CommonResponse;
import dynamicquad.agilehub.global.header.ReasonDto;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class ExceptionAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<Object> validation(ConstraintViolationException e, WebRequest request) {
        String errorMessage = e.getConstraintViolations().stream()
            .map(ConstraintViolation::getMessage)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                "ConstraintViolationException이 ConstraintViolation을 포함하지 않습니다."));

        ErrorStatus errorStatus = ErrorStatus.BAD_REQUEST;
        for (ErrorStatus value : ErrorStatus.values()) {
            if (value.name().equals(errorMessage)) {
                errorStatus = value;
                break;
            }
        }

        return handleExceptionInternalConstraint(e, errorStatus, HttpHeaders.EMPTY, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
                                                                  HttpHeaders headers, HttpStatusCode status,
                                                                  WebRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();

        e.getBindingResult().getFieldErrors()
            .forEach(fieldError -> {
                String fieldName = fieldError.getField();
                String errorMessage = Optional.ofNullable(fieldError.getDefaultMessage()).orElse("");
                errors.merge(fieldName, errorMessage,
                    (existingErrorMessage, newErrorMessage) -> existingErrorMessage + ", " + newErrorMessage);
            });

        return handleExceptionInternalArgs(e, HttpHeaders.EMPTY, ErrorStatus.BAD_REQUEST, request, errors);
    }

    @ExceptionHandler(value = GeneralException.class)
    public ResponseEntity<Object> handleGeneral(GeneralException generalException, HttpServletRequest request) {
        ReasonDto errorReasonHttpStatus = generalException.getErrorReasonHttpStatus();

        return handleExceptionInternalGeneral(generalException, errorReasonHttpStatus, null, request);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleOthers(Exception e, WebRequest request) {
        log.error("handleOthers", e);
        return handleExceptionInternalOthers(e, ErrorStatus.INTERNAL_SERVER_ERROR, HttpHeaders.EMPTY,
            ErrorStatus.INTERNAL_SERVER_ERROR.getHttpStatus(), request, e.getMessage());
    }

    private ResponseEntity<Object> handleExceptionInternalConstraint(Exception e, ErrorStatus errorStatus,
                                                                     HttpHeaders headers, WebRequest request) {
        CommonResponse<Object> body = CommonResponse.onFailure(errorStatus.getCode(),
            errorStatus.getMessage(),
            null);

        return super.handleExceptionInternal(e, body, headers, errorStatus.getHttpStatus(), request);
    }

    private ResponseEntity<Object> handleExceptionInternalArgs(Exception e, HttpHeaders headers,
                                                               ErrorStatus errorStatus,
                                                               WebRequest request, Map<String, String> errorArgs) {
        CommonResponse<Object> body = CommonResponse.onFailure(errorStatus.getCode(),
            errorStatus.getMessage(),
            errorArgs);

        return super.handleExceptionInternal(e, body, headers, errorStatus.getHttpStatus(), request);
    }

    private ResponseEntity<Object> handleExceptionInternalGeneral(Exception e, ReasonDto reason,
                                                                  HttpHeaders headers, HttpServletRequest request) {
        CommonResponse<Object> body = CommonResponse.onFailure(reason.getCode(),
            reason.getMessage(),
            null);

        return super.handleExceptionInternal(e, body, headers, reason.getStatus(), new ServletWebRequest(request));
    }

    private ResponseEntity<Object> handleExceptionInternalOthers(Exception e, ErrorStatus errorStatus,
                                                                 HttpHeaders headers, HttpStatus status,
                                                                 WebRequest request, String errorPoint) {
        CommonResponse<Object> body = CommonResponse.onFailure(errorStatus.getCode(),
            errorStatus.getMessage(),
            errorPoint);

        return super.handleExceptionInternal(e, body, headers, status, request);
    }
}
