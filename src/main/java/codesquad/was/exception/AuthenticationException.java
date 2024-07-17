package codesquad.was.exception;

import codesquad.was.http.common.HttpStatusCode;

public class AuthenticationException extends CommonException{
    public AuthenticationException(String message) {
        super(message, HttpStatusCode.UNAUTHORIZED);
    }
}
