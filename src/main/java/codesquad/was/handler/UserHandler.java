package codesquad.was.handler;

import codesquad.was.urlMapper.ResourceGetter;
import codesquad.was.urlMapper.UrlPathResourceMap;
import codesquad.was.exception.InternalServerException;
import codesquad.was.user.User;
import codesquad.was.user.UserRepository;
import codesquad.was.request.HttpRequest;
import codesquad.was.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

public class UserHandler implements Handler {
    private static final Logger log = LoggerFactory.getLogger(UserHandler.class);
    private final UserRepository userRepository;

    public UserHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void registration(HttpRequest request, HttpResponse response) throws InternalServerException, IOException {
        try {
            // 세개 중 하나라도 없으면 회원가입 로직 skip
            String userId = Optional.ofNullable(request.getParameter("userId")).orElseThrow(IllegalArgumentException::new);
            String username = Optional.ofNullable(request.getParameter("name")).orElseThrow(IllegalArgumentException::new);
            String password = Optional.ofNullable(request.getParameter("password")).orElseThrow(IllegalArgumentException::new);

            User user = User.factoryMethod(userId, username, password);
            userRepository.save(user);
            response.setStatusCode(200);

        } catch (IllegalArgumentException e) {
            log.debug("Invalid username or password or userId: {}", e.getMessage());
        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        } finally {
            //todo : 나중에 model 과 같은 객체를 사용하게 될 경우 리팩토링 필요
            //todo : 회원가입 로직을 수행하면 redirect 를 하게 할까?
            String resourcePath = UrlPathResourceMap.getResourcePathByUrlPath(request.getUrlPath());
            response.setBody(ResourceGetter.getResourceBytesByPath(resourcePath));
            response.setContentType(ResourceGetter.getContentTypeByPath(resourcePath));
        }
    }

    @Override
    public HttpResponse handleRequest(HttpRequest request) throws InternalServerException, IOException {
        HttpResponse response = new HttpResponse();
        registration(request,response);
        return response;
    }
}
