package codesquad.was.exception;

import codesquad.was.common.HttpStatusCode;

public class MethodNotAllowedException extends CommonException{
    public MethodNotAllowedException() {
        super("일치하는 메소드가 없습니다", HttpStatusCode.METHOD_NOT_ALLOWED);
    }
}
