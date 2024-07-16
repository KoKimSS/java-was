package codesquad.business.controller;

import codesquad.business.domain.Member;
import codesquad.was.http.common.HttpCookie;
import codesquad.was.http.common.HttpStatusCode;
import codesquad.was.exception.BadRequestException;
import codesquad.was.handler.Handler;
import codesquad.was.http.request.HttpRequest;
import codesquad.was.http.response.HttpResponse;
import codesquad.was.session.Manager;
import codesquad.was.session.Session;
import codesquad.business.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static codesquad.was.session.Session.*;
import static codesquad.was.session.Session.sessionStr;

public class LoginHandler implements Handler {
    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);
    private static int sessionLong = 600; //초
    private final MemberService memberService;

    // 싱글톤 으로 구현
    public static LoginHandler loginHandler = new LoginHandler(MemberService.memberService);

    private LoginHandler(MemberService memberService) {
        this.memberService = memberService;
    }

    @Override
    public HttpResponse handlePOSTRequest(HttpRequest request) throws BadRequestException {
        HttpResponse response = new HttpResponse();

        Member member = memberService.getUserByIdAndPw(request.getParameter("userId"), request.getParameter("password"));

        //로그인 실패시 redirect
        if (member == null) {
            HttpResponse.setRedirect(response, HttpStatusCode.FOUND, "/user/login_failed.html");
            return response;
        }

        Session session = new Session();
        String sessionId = createSessionId();
        session.setAttribute(userStr, member); // atttribute
        Manager.addSession(sessionId,session);

        //성공 시 main 으로
        HttpResponse.setRedirect(response, HttpStatusCode.FOUND, "/index.html");
        HttpCookie sessionCookie = new HttpCookie(sessionStr, sessionId);
        sessionCookie.setMaxAge(sessionLong);

        response.addCookie(sessionCookie);
        return response;
    }

}


