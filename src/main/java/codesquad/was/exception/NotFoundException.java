package codesquad.was.exception;

import codesquad.was.response.HTTPStatusCode;

public class NotFoundException extends CommonException{
    public NotFoundException(String message) {
        super(message, HTTPStatusCode.NOT_FOUND);
    }
}
