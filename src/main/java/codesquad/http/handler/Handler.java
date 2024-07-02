package codesquad.http.handler;

import codesquad.http.request.HttpRequest;
import codesquad.http.response.HttpResponse;
import codesquad.http.urlMapper.ResourceGetter;

import java.io.IOException;

import static codesquad.http.urlMapper.ResourceGetter.getResourceBytes;
import static codesquad.http.urlMapper.UrlResourceMap.getResourcePathByUrl;

public class Handler {

    private final UserHandler userHandler;

    public Handler(UserHandler userHandler) {
        this.userHandler = userHandler;
    }

    public HttpResponse handlerMapping(HttpRequest request) throws IOException {
        HttpResponse response = new HttpResponse();
        System.out.println(request.getUri()+" "+request.getMethod());

        // URL 매핑이 되면 비즈니스 로직 수행
        if(request.getMethod().equals("GET") && request.getUri().equals("/registration")) {
            userHandler.saveUser(request,response);
            return response;
        }

        // URL 매핑이 되지 않으면 정적인 파일만 보냄
        String resourcePath = getResourcePathByUrl(request.getUrl());
        byte[] body = getResourceBytes(resourcePath);
        response.setBody(body);
        response.setStatusCode(200);
        response.setContentType(ResourceGetter.getContentTypeByPath(resourcePath));
        return response;
    }
}
