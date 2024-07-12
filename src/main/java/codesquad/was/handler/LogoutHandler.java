package codesquad.was.handler;

import codesquad.was.common.HttpCookie;
import codesquad.was.common.HttpStatusCode;
import codesquad.was.exception.BadRequestException;
import codesquad.was.exception.InternalServerException;
import codesquad.was.exception.MethodNotAllowedException;
import codesquad.was.repository.UserRepository;
import codesquad.was.request.HttpRequest;
import codesquad.was.response.HttpResponse;
import codesquad.was.session.Manager;
import codesquad.was.session.Session;
import codesquad.was.user.User;

import java.io.IOException;

import static codesquad.was.session.Session.*;

public class LogoutHandler implements Handler{
    public static final LogoutHandler logoutHandler = new LogoutHandler();

    @Override
    public HttpResponse handlePOSTRequest(HttpRequest request) {
        HttpResponse response = new HttpResponse();
        Session session = request.getSession();

        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new BadRequestException("You are not logged in");
        }

        Manager.removeSession(request.getCookies().get(sessionStr));
        HttpCookie cookie = new HttpCookie(sessionStr, request.getCookies().get(sessionStr));
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        HttpResponse.setRedirect(response, HttpStatusCode.FOUND,"/");
        return response;
    }
}
