package codesquad.was.exception;

import codesquad.was.common.HTTPStatusCode;

public class CommonException extends Exception{
    private HTTPStatusCode httpStatusCode;
    private String message;

    public CommonException() {
    }

    public CommonException(String message, HTTPStatusCode httpStatusCode) {
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }


    @Override
    public String getMessage() {
        return message;
    }

    public HTTPStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }

}
