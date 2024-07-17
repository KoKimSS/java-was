package codesquad.business.controller;

import codesquad.business.domain.Article;
import codesquad.business.domain.Comment;
import codesquad.business.domain.Member;
import codesquad.business.service.ArticleService;
import codesquad.business.service.CommentService;
import codesquad.was.exception.BadRequestException;
import codesquad.was.handler.Handler;
import codesquad.was.http.common.HttpStatusCode;
import codesquad.was.http.common.Mime;
import codesquad.was.http.request.HttpRequest;
import codesquad.was.http.response.HttpResponse;
import codesquad.was.render.HtmlTemplateRender;
import codesquad.was.render.Model;
import codesquad.was.render.Render;
import codesquad.was.session.Session;
import codesquad.was.util.ResourceGetter;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static codesquad.was.render.HtmlTemplateRender.*;

public class ArticleDetailHandler implements Handler {
    private final ArticleService articleService;
    private final CommentService commentService;
    public static final ArticleDetailHandler articleDetailHandler = new ArticleDetailHandler(ArticleService.articleService,CommentService.commentService);

    public ArticleDetailHandler(ArticleService articleService, CommentService commentService) {
        this.articleService = articleService;
        this.commentService = commentService;
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
        Member member = (Member) request.getSession().getAttribute(Session.userStr);

        if(member != null) {
            model.addSingleData("userName",member.getUsername());
        }

        model.addSingleData("article",article);

        List<Comment> comments = commentService.getListByArticleId(article.getId());
        model.addListData("comment",new ArrayList<>(comments));

        response.setBody(render(htmlBody, model).getBytes(StandardCharsets.UTF_8));
        return response;
    }
}
