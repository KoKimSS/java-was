package codesquad.was.handler;

import codesquad.was.common.HttpCookie;
import codesquad.was.common.HttpStatusCode;
import codesquad.was.exception.BadRequestException;
import codesquad.was.exception.InternalServerException;
import codesquad.was.request.HttpRequest;
import codesquad.was.response.HttpResponse;
import codesquad.was.session.Manager;
import codesquad.was.session.Session;
import codesquad.was.user.User;
import codesquad.was.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static codesquad.was.session.Session.sessionStr;

public class LoginHandler implements Handler {
    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);
    private static int sessionLong = 30; //초

    private final UserRepository userRepository;

    // 싱글톤 으로 구현
    public static LoginHandler loginHandler = new LoginHandler(new UserRepository());

    private LoginHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public HttpResponse handlePOSTRequest(HttpRequest request) throws BadRequestException {
        HttpResponse response = new HttpResponse();

        User user = userRepository.getUserByIdAndPw(request.getParameter("userId"), request.getParameter("password"));

        //로그인 실패시 redirect
        if (user == null) {
            HttpResponse.setRedirect(response, HttpStatusCode.FOUND, "/user/login_failed.html");
            return response;
        }

        Session session = new Session();
        String sessionId = Session.createSessionId();
        session.setAttribute("user",user);
        Manager.addSession(sessionId,session);

        //성공 시 main 으로
        HttpResponse.setRedirect(response, HttpStatusCode.FOUND, "/index.html");
        HttpCookie sessionCookie = new HttpCookie(sessionStr, sessionId);
        sessionCookie.setMaxAge(sessionLong);

        HttpCookie cookie = new HttpCookie("cookie", "123456");
        response.addCookie(sessionCookie);
        response.addCookie(cookie);
        return response;
    }

    @Override
    public HttpResponse handleGETRequest(HttpRequest request) {
        HttpResponse response = new HttpResponse();

        return null;
    }

    @Override
    public HttpResponse handlePUTRequest(HttpRequest request) {
        return null;
    }

    @Override
    public HttpResponse handleDELETERequest(HttpRequest request) {
        return null;
    }
}


