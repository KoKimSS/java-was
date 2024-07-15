package codesquad.business.controller;

import codesquad.business.domain.User;
import codesquad.was.handler.Handler;
import codesquad.was.http.common.HttpStatusCode;
import codesquad.was.http.common.Mime;
import codesquad.was.http.request.HttpRequest;
import codesquad.was.http.response.HttpResponse;
import codesquad.was.render.HtmlTemplateRender;
import codesquad.was.render.Model;
import codesquad.was.session.Session;

import java.nio.charset.StandardCharsets;

import static codesquad.was.util.ResourceGetter.getResourceBytesByPath;

public class ArticleHandler implements Handler {

    public static ArticleHandler articleHandler = new ArticleHandler();

    @Override
    public HttpResponse handleGETRequest(HttpRequest request) {
        HttpResponse response = new HttpResponse();

        Model model = new Model();
        User user = (User)request.getSession().getAttribute(Session.userStr);

        if(user == null) {
            HttpResponse.setRedirect(response, HttpStatusCode.FOUND,"/login");
            return response;
        }

        model.addSingleData("userName",user.getUsername());
        byte[] htmlBytes = getResourceBytesByPath("/static/article/index.html");
        response.setStatusCode(HttpStatusCode.OK);
        response.setContentType(Mime.TEXT_HTML);

        String renderedHtml = HtmlTemplateRender.render(new String(htmlBytes, StandardCharsets.UTF_8), model);
        response.setBody(renderedHtml.getBytes(StandardCharsets.UTF_8));
        return response;
    }

    @Override
    public HttpResponse handlePOSTRequest(HttpRequest request) {
        return Handler.super.handlePOSTRequest(request);
    }
}