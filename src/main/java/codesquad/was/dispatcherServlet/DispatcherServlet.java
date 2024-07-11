package codesquad.was.dispatcherServlet;

import codesquad.was.exception.CommonException;
import codesquad.was.exception.MethodNotAllowedException;
import codesquad.was.exception.NotFoundException;
import codesquad.was.handler.Handler;
import codesquad.was.handler.HandlerMap;
import codesquad.was.request.HttpRequest;
import codesquad.was.common.HttpStatusCode;
import codesquad.was.response.HttpResponse;
import codesquad.was.util.ResourceGetter;
import codesquad.was.util.UrlPathResourceMap;

import java.io.IOException;

public class DispatcherServlet {

    public HttpResponse callHandler(HttpRequest request) throws IOException{
        Handler handler = HandlerMap.getHandler(request.getUrlPath());

        // url 과 매핑이 안되면
        if (handler == null) {
            return staticResponse(request);
        }

        try {
            return handler.doBusinessByMethod(request);
        } catch (CommonException e) { // method 와 매핑이 안되면
            return staticResponse(request);
        }
    }

    private static HttpResponse staticResponse(HttpRequest request) throws IOException, NotFoundException {
        HttpResponse response = new HttpResponse();
        // URL 매핑이 되지 않으면 정적인 파일만 보냄
        String urlPath = request.getUrl().getPath();
        String resourcePath = UrlPathResourceMap.getResourcePathByUrlPath(urlPath);

        byte[] body = ResourceGetter.getResourceBytesByPath(resourcePath);

        response.setBody(body);
        response.setStatusCode(HttpStatusCode.OK);
        response.setContentType(ResourceGetter.getContentTypeByPath(resourcePath));

        return response;
    }
}