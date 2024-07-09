package codesquad.was.exception;

import codesquad.was.common.HTTPStatusCode;

public class InternalServerException extends CommonException{
    public InternalServerException(String message) {
        super(message, HTTPStatusCode.INTERNAL_SERVER_ERROR);
    }
}
