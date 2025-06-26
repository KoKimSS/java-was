package codesquad.business.controller;

import codesquad.business.service.MemberService;
import codesquad.was.handler.Handler;
import codesquad.was.http.request.HttpRequest;
import codesquad.was.http.response.HttpResponse;
import codesquad.was.http.common.HttpStatusCode;

import java.nio.charset.StandardCharsets;

/**
 * 성능 테스트용 간단한 핸들러
 */
public class TestHandler implements Handler {

    public static TestHandler testHandler = new TestHandler();

    @Override
    public HttpResponse handleGETRequest(HttpRequest request) {
        HttpResponse response = new HttpResponse();
        response.setStatusCode(HttpStatusCode.OK);
        response.setStatusMessage("OK");
        String res = "Hello from " + getServerType() + " Server!";
        response.setBody(res.getBytes(StandardCharsets.UTF_8));
        response.addHeader("Content-Type", "text/plain");
        return response;
    }
    
    /**
     * 서버 타입 확인 (스레드 이름으로 구분)
     */
    private String getServerType() {
        String threadName = Thread.currentThread().getName();
        if (threadName.contains("pool")) {
            return "AIO";
        } else {
            return "NIO";
        }
    }
}
