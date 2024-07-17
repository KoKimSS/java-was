package codesquad.business.controller;

import codesquad.business.domain.Article;
import codesquad.business.domain.Member;
import codesquad.business.service.ArticleService;
import codesquad.was.exception.BadRequestException;
import codesquad.was.handler.Handler;
import codesquad.was.http.common.File;
import codesquad.was.http.common.HttpStatusCode;
import codesquad.was.http.common.Mime;
import codesquad.was.http.request.HttpRequest;
import codesquad.was.http.response.HttpResponse;
import codesquad.was.render.HtmlTemplateRender;
import codesquad.was.render.Model;
import codesquad.was.session.Session;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static codesquad.business.domain.Article.*;
import static codesquad.was.util.ResourceGetter.getResourceBytesByPath;

public class ArticleHandler implements Handler {

    public static ArticleHandler articleHandler = new ArticleHandler(ArticleService.articleService);

    private final ArticleService articleService;

    public ArticleHandler(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Override
    public HttpResponse handleGETRequest(HttpRequest request) {
        HttpResponse response = new HttpResponse();

        Model model = new Model();
        Member member = (Member)request.getSession().getAttribute(Session.userStr);

        if(member == null) {
            HttpResponse.setRedirect(response, HttpStatusCode.FOUND,"/login");
            return response;
        }

        model.addSingleData("userName", member.getUsername());
        byte[] htmlBytes = getResourceBytesByPath("/static/article/index.html");
        response.setStatusCode(HttpStatusCode.OK);
        response.setContentType(Mime.TEXT_HTML);

        String renderedHtml = HtmlTemplateRender.render(new String(htmlBytes, StandardCharsets.UTF_8), model);
        response.setBody(renderedHtml.getBytes(StandardCharsets.UTF_8));
        return response;
    }

    @Override
    public HttpResponse handlePOSTRequest(HttpRequest request) {
        HttpResponse response = new HttpResponse();

        String contents = request.getParameter("contents");
        if(contents == null || contents.isEmpty()) {
            throw new BadRequestException("content is empty");
        }

        String title = request.getParameter("title");
        if(title == null || title.isEmpty()) {
            throw new BadRequestException("content is empty");
        }

        List<File> files = request.getFiles();

        if(files != null && !files.isEmpty()) {
            for(File file : files) {
                File.save(file);
            }
        }

        Member member = (Member)request.getSession().getAttribute(Session.userStr);
        if(member == null) {
            HttpResponse.setRedirect(response, HttpStatusCode.FOUND,"/login");
            return response;
        }

        Article article = FactoryMethod(title, contents, member.getId());
        long articleId = articleService.saveArticle(article);
        System.out.println("아티클 생성"+articleId);

        HttpResponse.setRedirect(response, HttpStatusCode.FOUND, "/index.html");
        return response;
    }
}
