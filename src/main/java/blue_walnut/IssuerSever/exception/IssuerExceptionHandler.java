package blue_walnut.IssuerSever.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class IssuerExceptionHandler extends ResponseEntityExceptionHandler {
    // 토큰 예외 처리
    @ExceptionHandler(ApprovalException.class)
    public ResponseEntity<ErrorResponse> handleTokenRegistryException(ApprovalException ex) {
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 결제 예외 처리
    @ExceptionHandler(TokenException.class)
    public ResponseEntity<ErrorResponse> handlePaymentProcessingException(TokenException ex) {
        return buildErrorResponse(ex, HttpStatus.PAYMENT_REQUIRED);
    }

    // 결제 예외 처리
    @ExceptionHandler(RetryableException.class)
    public ResponseEntity<ErrorResponse> handlePaymentProcessingException(RetryableException ex) {
        return buildErrorResponse(ex, HttpStatus.PAYMENT_REQUIRED);
    }

    // 그 외의 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage());
        return buildErrorResponse(new RuntimeException("알 수 없는 오류가 발생했습니다."), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 에러 응답 객체 생성
    private ResponseEntity<ErrorResponse> buildErrorResponse(RuntimeException ex, HttpStatus status) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), getErrorCode(ex));
        log.error("Error occurred: {} - {}", errorResponse.getCode(), errorResponse.getMessage());
        return new ResponseEntity<>(errorResponse, status);
    }

    // 예외 객체에서 ErrorCode 추출
    private String getErrorCode(RuntimeException ex) {
        if (ex instanceof ApprovalException) {
            return ((ApprovalException) ex).getCode();
        } else if (ex instanceof TokenException) {
            return ((TokenException) ex).getCode();
        } else if (ex instanceof RetryableException) {
            return ((RetryableException) ex).getCode();
        }
        return "9999"; // 기본 시스템 오류 코드
    }

    // 에러 응답 구조
    public static class ErrorResponse {
        private final String message;
        private final String code;

        public ErrorResponse(String message, String code) {
            this.message = message;
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public String getCode() {
            return code;
        }
    }
}
