package codesquad.was.dispatcherServlet;

import codesquad.was.exception.CommonException;
import codesquad.was.exception.MethodNotAllowedException;
import codesquad.was.exception.NotFoundException;
import codesquad.was.handler.Handler;
import codesquad.was.handler.HandlerMap;
import codesquad.was.render.HtmlTemplateRender;
import codesquad.was.render.Model;
import codesquad.was.http.request.HttpRequest;
import codesquad.was.http.common.HttpStatusCode;
import codesquad.was.http.response.HttpResponse;
import codesquad.was.session.Session;
import codesquad.business.domain.User;
import codesquad.was.util.ResourceGetter;
import codesquad.was.util.UrlPathResourceMap;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class DispatcherServlet {
    private final HandlerMap handlerMap = HandlerMap.factoryMethod();

    public HttpResponse callHandler(HttpRequest request) throws IOException{
        Handler handler = handlerMap.getHandler(request.getUrlPath());

        // url 과 매핑이 안되면
        if (handler == null) {
            return staticResponse(request);
        }

        try {
            return handler.doBusinessByMethod(request);
        } catch (MethodNotAllowedException e) { // method 와 매핑이 안되면
            return staticResponse(request);
        }
    }

    private static HttpResponse staticResponse(HttpRequest request) throws IOException, NotFoundException {
        HttpResponse response = new HttpResponse();
        // URL 매핑이 되지 않으면 정적인 파일만 보냄
        String urlPath = request.getUrl().getPath();
        String resourcePath = UrlPathResourceMap.getResourcePathByUrlPath(urlPath);

        response.setStatusCode(HttpStatusCode.OK);
        response.setContentType(ResourceGetter.getContentTypeByPath(resourcePath));

        byte[] htmlBytes = ResourceGetter.getResourceBytesByPath(resourcePath);

        Model model = new Model();
        User user = (User) request.getSession().getAttribute(Session.userStr);

        if (user != null) {
            System.out.println("로그인 된 요청");
            model.addSingleData("userName",user.getUsername());
        }

        String renderedHtml = HtmlTemplateRender.render(new String(htmlBytes, StandardCharsets.UTF_8), model);
        response.setBody(renderedHtml.getBytes(StandardCharsets.UTF_8));
        System.out.println("정적 파일 전송");
        return response;
    }
}