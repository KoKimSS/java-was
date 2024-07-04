package codesquad.was.dispatcherServlet;

import codesquad.was.exception.InternalServerException;
import codesquad.was.handler.Handler;
import codesquad.was.handler.HandlerMap;
import codesquad.was.request.HttpRequest;
import codesquad.was.response.HttpResponse;
import codesquad.was.urlMapper.ResourceGetter;
import codesquad.was.urlMapper.UrlPathResourceMap;

import java.io.IOException;

public class DispatcherServlet {

    public HttpResponse callHandler(HttpRequest request) throws IOException, InternalServerException {
        Handler handler = HandlerMap.getHandler(request.getMethod() , request.getUrlPath());

        if (handler == null) {
            return staticResponse(request);
        }

        return handler.handleRequest(request);
    }

    private static HttpResponse staticResponse(HttpRequest request) throws IOException {
        // URL 매핑이 되지 않으면 정적인 파일만 보냄
        HttpResponse response = new HttpResponse();
        String resourcePath = UrlPathResourceMap.getResourcePathByUrlPath(request.getUrl().getPath());
        byte[] body = ResourceGetter.getResourceBytesByPath(resourcePath);

        response.setBody(body);
        response.setStatusCode(200);
        response.setContentType(ResourceGetter.getContentTypeByPath(resourcePath));

        return response;
    }
}
