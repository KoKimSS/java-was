package codesquad.was.handler;

import codesquad.was.request.HttpRequest;
import codesquad.was.response.HttpResponse;
import codesquad.was.urlMapper.ResourceGetter;
import codesquad.was.urlMapper.UrlPathResourceMap;
import codesquad.was.exception.InternalServerException;

import java.io.IOException;

public class Handler {
    private final UserHandler userHandler;

    public Handler(UserHandler userHandler) {
        this.userHandler = userHandler;
    }

    public HttpResponse handlerMapping(HttpRequest request) throws IOException, InternalServerException {
        HttpResponse response = new HttpResponse();

        System.out.println(request.getUrlPath()+" "+"요청주소");
        // URL 매핑이 되면 비즈니스 로직 수행
        System.out.println("request.getMethod() = " + request.getMethod());
        if(request.getMethod().equals("GET") && request.getUrlPath().equals("/registration")) {
            System.out.println("유저 핸들러 실행");
            userHandler.registration(request,response);
            return response;
        }

        staticResponse(request, response);
        return response;
    }

    private static void staticResponse(HttpRequest request, HttpResponse response) throws IOException {
        // URL 매핑이 되지 않으면 정적인 파일만 보냄
        String resourcePath = UrlPathResourceMap.getResourcePathByUrlPath(request.getUrl().getPath());
        byte[] body = ResourceGetter.getResourceBytesByPath(resourcePath);

        response.setBody(body);
        response.setStatusCode(200);
        response.setContentType(ResourceGetter.getContentTypeByPath(resourcePath));
    }
}
