package codesquad.business.controller;

import codesquad.business.domain.Comment;
import codesquad.business.domain.Member;
import codesquad.business.service.CommentService;
import codesquad.was.exception.AuthenticationException;
import codesquad.was.handler.Handler;
import codesquad.was.http.common.HttpStatusCode;
import codesquad.was.http.request.HttpRequest;
import codesquad.was.http.response.HttpResponse;
import codesquad.was.session.Session;

public class CommentHandler implements Handler {

    private final CommentService commentService;

    public static final CommentHandler commentHandler = new CommentHandler(CommentService.commentService);

    public CommentHandler(CommentService commentService) {
        this.commentService = commentService;
    }

    @Override
    public HttpResponse handlePOSTRequest(HttpRequest request) {
        HttpResponse response = new HttpResponse();
        String contents = request.getParameter("contents");
        Long articleId = Long.parseLong(request.getParameter("articleId"));

        Session session = request.getSession();
        if (session == null || session.getAttribute(Session.userStr) == null) {
            throw new AuthenticationException("로그인 되야 합니다");
        }

        Member member = (Member) session.getAttribute(Session.userStr);
        commentService.save(Comment.factoryMethod(contents, member.getId(), articleId));
        HttpResponse.setRedirect(response, HttpStatusCode.FOUND,"/article/list");
        return response;
    }
}
