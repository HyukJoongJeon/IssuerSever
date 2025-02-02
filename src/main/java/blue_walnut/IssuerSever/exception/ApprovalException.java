package blue_walnut.IssuerSever.exception;

public class ApprovalException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String errorMessage;

    public ApprovalException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getMessage();
    }
    public String getCode() {
        return errorCode.getCode();
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
