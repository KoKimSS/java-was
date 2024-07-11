package codesquad.was.handler;

import codesquad.was.common.HttpStatusCode;
import codesquad.was.exception.InternalServerException;
import codesquad.was.exception.MethodNotAllowedException;
import codesquad.was.request.HttpRequest;
import codesquad.was.response.HttpResponse;
import codesquad.was.session.Session;
import codesquad.was.user.UserService;

import java.io.IOException;

import static codesquad.was.session.Session.*;

public class UserListHandler implements Handler{

    UserService userService = new UserService();

    public static UserListHandler userListHandler = new UserListHandler();

    @Override
    public HttpResponse handleGETRequest(HttpRequest request) throws InternalServerException, IOException, MethodNotAllowedException {
        HttpResponse response = new HttpResponse();
        Session session = request.getSession();

        if(session==null || session.getAttribute(userStr)==null) {
            HttpResponse.setRedirect(response, HttpStatusCode.FOUND,"/login");
            return response;
        }

        userService.


        return null;
    }


}
