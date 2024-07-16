package codesquad.was.exception;

import codesquad.was.http.common.HttpStatusCode;

public class DBException extends CommonException{
    public DBException(String message) {
        super(message, HttpStatusCode.INTERNAL_SERVER_ERROR);
    }
}
