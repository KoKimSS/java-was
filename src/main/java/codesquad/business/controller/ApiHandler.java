package codesquad.business.controller;

import codesquad.was.handler.Handler;
import codesquad.was.http.common.HttpStatusCode;
import codesquad.was.http.common.Mime;
import codesquad.was.http.request.HttpRequest;
import codesquad.was.http.response.HttpResponse;

import java.nio.charset.StandardCharsets;

public class ApiHandler implements Handler {

    @Override
    public HttpResponse handleGETRequest(HttpRequest request) {
        HttpResponse response = new HttpResponse();
        response.setStatusCode(HttpStatusCode.OK);
        response.setContentType(Mime.TEXT_PLAIN);
        String htmlBody = "외부 api 호출";

        
        response.setBody(htmlBody.getBytes(StandardCharsets.UTF_8));
        return response;
    }
}
