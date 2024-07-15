package codesquad.was.exception;

import codesquad.was.http.common.HttpStatusCode;

public class BadRequestException extends CommonException{
    public BadRequestException(String message) {
        super(message, HttpStatusCode.BAD_REQUEST);
    }
}
