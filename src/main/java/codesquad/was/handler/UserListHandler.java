package codesquad.was.handler;

import codesquad.was.common.HttpStatusCode;
import codesquad.was.exception.NotFoundException;
import codesquad.was.mime.Mime;
import codesquad.was.render.HtmlTemplateRender;
import codesquad.was.render.Model;
import codesquad.was.request.HttpRequest;
import codesquad.was.response.HttpResponse;
import codesquad.was.session.Session;
import codesquad.was.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static codesquad.was.repository.UserRepository.*;
import static codesquad.was.session.Session.*;
import static codesquad.was.util.ResourceGetter.getResourceBytesByPath;

public class UserListHandler implements Handler{

    public static UserListHandler userListHandler = new UserListHandler();
    private static final Logger log = LoggerFactory.getLogger(UserListHandler.class);

    @Override
    public HttpResponse handleGETRequest(HttpRequest request) throws IOException, NotFoundException {
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

        String renderedHtml = HtmlTemplateRender.render(new String(htmlBytes,StandardCharsets.UTF_8), model);
        log.debug(renderedHtml);
        response.setStatusCode(HttpStatusCode.OK);
        response.setContentType(Mime.TEXT_HTML.getMimeType());
        response.setBody(renderedHtml.getBytes(StandardCharsets.UTF_8));


        return response;
    }


}
