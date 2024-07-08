package codesquad.was.dispatcherServlet;

import codesquad.was.exception.InternalServerException;
import codesquad.was.exception.NotFoundException;
import codesquad.was.handler.Handler;
import codesquad.was.handler.HandlerMap;
import codesquad.was.request.HttpRequest;
import codesquad.was.response.HTTPStatusCode;
import codesquad.was.response.HttpResponse;
import codesquad.was.util.ResourceGetter;
import codesquad.was.util.UrlPathResourceMap;

import java.io.IOException;

public class DispatcherServlet {

    public HttpResponse callHandler(HttpRequest request) throws IOException, InternalServerException {
        Handler handler = HandlerMap.getHandler(request.getMethod() , request.getUrlPath());
        HttpResponse response = new HttpResponse();
        if (handler == null) {
            try {
                return staticResponse(request,response);
            } catch (NotFoundException e) {
                response.setStatusCode(e.getHttpStatusCode());
                System.out.println("에러코드:"+e.getHttpStatusCode().getCode());
                return response;
            }
        }

        return handler.handleRequest(request);
    }

    private static HttpResponse staticResponse(HttpRequest request,HttpResponse response) throws IOException, NotFoundException {
        // URL 매핑이 되지 않으면 정적인 파일만 보냄
        String urlPath = request.getUrl().getPath();
        System.out.println("urlPath = " + urlPath);
        String resourcePath = UrlPathResourceMap.getResourcePathByUrlPath(urlPath);
        System.out.println("리로스 패스"+resourcePath);

        byte[] body = ResourceGetter.getResourceBytesByPath(resourcePath);

        response.setBody(body);
        response.setStatusCode(HTTPStatusCode.OK);
        response.setContentType(ResourceGetter.getContentTypeByPath(resourcePath));

        return response;
    }
}