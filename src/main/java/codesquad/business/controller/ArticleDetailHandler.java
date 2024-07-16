package codesquad.business.controller;

import codesquad.business.domain.Article;
import codesquad.business.domain.Comment;
import codesquad.business.service.ArticleService;
import codesquad.was.exception.BadRequestException;
import codesquad.was.handler.Handler;
import codesquad.was.http.common.HttpStatusCode;
import codesquad.was.http.common.Mime;
import codesquad.was.http.request.HttpRequest;
import codesquad.was.http.response.HttpResponse;
import codesquad.was.render.HtmlTemplateRender;
import codesquad.was.render.Model;
import codesquad.was.render.Render;
import codesquad.was.util.ResourceGetter;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static codesquad.was.render.HtmlTemplateRender.*;

public class ArticleDetailHandler implements Handler {
    private final ArticleService articleService;

    public static final ArticleDetailHandler articleDetailHandler = new ArticleDetailHandler(ArticleService.articleService);

    public ArticleDetailHandler(ArticleService articleService) {
        this.articleService = articleService;
    }


    @Override
    public HttpResponse handleGETRequest(HttpRequest request) {
        HttpResponse response = new HttpResponse();

        Long id = Long.parseLong(request.getParameter("id"));
        Article article = articleService.findById(id);

        if(article == null) {
            throw new BadRequestException("Article not found");
        }


        response.setStatusCode(HttpStatusCode.OK);
        response.setContentType(Mime.TEXT_HTML);
        byte[] resourceBytesByPath = ResourceGetter.getResourceBytesByPath("/static/articleDetails/index.html");
        String htmlBody = new String(resourceBytesByPath, StandardCharsets.UTF_8);

        Model model = new Model();
        model.addSingleData("article",article);
        model.addListData("comment", List.of(new Comment(1L,"댓글1",1L),
                new Comment(2L,"댓글2",2L)));

        response.setBody(render(htmlBody, model).getBytes(StandardCharsets.UTF_8));
        return response;
    }
}
