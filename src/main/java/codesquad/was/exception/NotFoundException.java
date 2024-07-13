package codesquad.was.exception;

import codesquad.was.http.common.HttpStatusCode;

public class NotFoundException extends CommonException{
    public NotFoundException(String message) {
        super(message, HttpStatusCode.NOT_FOUND);
    }
}
