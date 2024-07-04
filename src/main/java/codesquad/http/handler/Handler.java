package codesquad.http.handler;

import codesquad.http.request.HttpRequest;
import codesquad.http.response.HttpResponse;
import codesquad.http.urlMapper.ResourceGetter;

import java.io.IOException;

import static codesquad.http.urlMapper.ResourceGetter.getResourceBytesByPath;
import static codesquad.http.urlMapper.UrlResourceMap.getResourcePathByUri;

public class Handler {
    private final UserHandler userHandler;

    public Handler(UserHandler userHandler) {
        this.userHandler = userHandler;
    }

    public HttpResponse handlerMapping(HttpRequest request) throws IOException {
        HttpResponse response = new HttpResponse();

        // URL 매핑이 되면 비즈니스 로직 수행
        if(request.getMethod().equals("GET") && request.getUrlPath().equals("/registration")) {
            userHandler.registration(request,response);
            return response;
        }

        staticResponse(request, response);
        return response;
    }

    private static void staticResponse(HttpRequest request, HttpResponse response) throws IOException {
        // URL 매핑이 되지 않으면 정적인 파일만 보냄
        String resourcePath = getResourcePathByUri(request.getUrl().toString());
        byte[] body = getResourceBytesByPath(resourcePath);

        response.setBody(body);
        response.setStatusCode(200);
        response.setContentType(ResourceGetter.getContentTypeByPath(resourcePath));
    }
}
