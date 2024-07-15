package codesquad.was.exception;

import codesquad.was.http.common.HttpStatusCode;

public class CommonException extends RuntimeException{
    private HttpStatusCode httpStatusCode;
    private String message;

    public CommonException() {
    }

    public CommonException(String message, HttpStatusCode httpStatusCode) {
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }


    @Override
    public String getMessage() {
        return message;
    }

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }

}
