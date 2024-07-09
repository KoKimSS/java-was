package codesquad.was.exception;

import codesquad.was.common.HTTPStatusCode;

public class MethodNotAllowedException extends CommonException{
    public MethodNotAllowedException() {
        super("일치하는 메소드가 없습니다", HTTPStatusCode.METHOD_NOT_ALLOWED);
    }
}
