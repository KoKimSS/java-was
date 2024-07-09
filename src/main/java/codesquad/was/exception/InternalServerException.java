package codesquad.was.exception;

import codesquad.was.common.HttpStatusCode;

public class InternalServerException extends CommonException{
    public InternalServerException(String message) {
        super(message, HttpStatusCode.INTERNAL_SERVER_ERROR);
    }
}
