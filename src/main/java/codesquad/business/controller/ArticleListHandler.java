package codesquad.business.controller;

import codesquad.business.domain.Article;
import codesquad.business.domain.Member;
import codesquad.business.service.ArticleService;
import codesquad.was.handler.Handler;
import codesquad.was.http.common.HttpStatusCode;
import codesquad.was.http.common.Mime;
import codesquad.was.http.request.HttpRequest;
import codesquad.was.http.response.HttpResponse;
import codesquad.was.render.HtmlTemplateRender;
import codesquad.was.render.Model;
import codesquad.was.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static codesquad.was.session.Session.userStr;
import static codesquad.was.util.ResourceGetter.getResourceBytesByPath;

public class ArticleListHandler implements Handler {

    private final ArticleService articleService;
    private static final Logger log = LoggerFactory.getLogger(ArticleListHandler.class);


    public static ArticleListHandler articleListHandler = new ArticleListHandler(ArticleService.articleService);

    public ArticleListHandler(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Override
    public HttpResponse handleGETRequest(HttpRequest request) {
        HttpResponse response = new HttpResponse();
        List<Article> allArticles = articleService.getAllArticles();
        Model model = new Model();
        model.addListData("articles",new ArrayList<>(allArticles));


        Session session = request.getSession();
        if(session != null && session.getAttribute(userStr) != null){
            model.addSingleData("userName", ((Member) session.getAttribute(userStr)).getUsername());
        }

        byte[] htmlBytes = getResourceBytesByPath("/static/articleList/index.html");
        response.setStatusCode(HttpStatusCode.OK);
        response.setContentType(Mime.TEXT_HTML);

        String renderedHtml = HtmlTemplateRender.render(new String(htmlBytes, StandardCharsets.UTF_8), model);
        log.debug(renderedHtml);
        response.setBody(renderedHtml.getBytes(StandardCharsets.UTF_8));

        return response;
    }
}
