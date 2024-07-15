package codesquad.business.controller;

import codesquad.was.http.common.HttpStatusCode;
import codesquad.was.exception.NotFoundException;
import codesquad.was.handler.Handler;
import codesquad.was.http.common.Mime;
import codesquad.was.render.HtmlTemplateRender;
import codesquad.was.render.Model;
import codesquad.was.http.request.HttpRequest;
import codesquad.was.http.response.HttpResponse;
import codesquad.was.session.Session;
import codesquad.business.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static codesquad.business.repository.UserRepository.*;
import static codesquad.was.session.Session.*;
import static codesquad.was.util.ResourceGetter.getResourceBytesByPath;

public class UserListHandler implements Handler {

    public static UserListHandler userListHandler = new UserListHandler();
    private static final Logger log = LoggerFactory.getLogger(UserListHandler.class);

    @Override
    public HttpResponse handleGETRequest(HttpRequest request){
        HttpResponse response = new HttpResponse();
        Session session = request.getSession();

        if(session==null || session.getAttribute(userStr) ==null) {
            HttpResponse.setRedirect(response, HttpStatusCode.FOUND,"/login");
            return response;
        }
        List<Object> userList = userRepository.getAllObject();
        Model model = new Model();
        model.addListData("users", userList);
        model.addSingleData("userName", ((User) session.getAttribute(userStr)).getUsername());

        byte[] htmlBytes = getResourceBytesByPath("/static/userList/index.html");
        response.setStatusCode(HttpStatusCode.OK);
        response.setContentType(Mime.TEXT_HTML);

        String renderedHtml = HtmlTemplateRender.render(new String(htmlBytes,StandardCharsets.UTF_8), model);
        log.debug(renderedHtml);
        response.setBody(renderedHtml.getBytes(StandardCharsets.UTF_8));


        return response;
    }


}