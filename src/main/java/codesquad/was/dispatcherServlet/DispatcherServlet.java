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

        try {
            if (handler == null) {
                return staticResponse(request);
            }
            HttpResponse response = handler.doBusinessByMethod(request);
            if(response == null) throw new MethodNotAllowedException();
            return response;
        } catch (CommonException e) {
            HttpResponse response = new HttpResponse();
            response.setStatusCode(e.getHttpStatusCode());
            System.out.println("에러코드:"+e.getHttpStatusCode().getCode());
            return response;
        }
    }

    private static HttpResponse staticResponse(HttpRequest request) throws IOException, NotFoundException {
        HttpResponse response = new HttpResponse();
        // URL 매핑이 되지 않으면 정적인 파일만 보냄
        String urlPath = request.getUrl().getPath();
        System.out.println("urlPath = " + urlPath);
        String resourcePath = UrlPathResourceMap.getResourcePathByUrlPath(urlPath);
        System.out.println("리로스 패스"+resourcePath);

        byte[] body = ResourceGetter.getResourceBytesByPath(resourcePath);

        response.setBody(body);
        response.setStatusCode(HttpStatusCode.OK);
        response.setContentType(ResourceGetter.getContentTypeByPath(resourcePath));

        return response;
    }
}