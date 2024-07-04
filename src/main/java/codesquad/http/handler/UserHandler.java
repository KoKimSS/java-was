package codesquad.http.handler;

import codesquad.http.user.User;
import codesquad.http.user.UserRepository;
import codesquad.http.request.HttpRequest;
import codesquad.http.response.HttpResponse;
import codesquad.http.urlMapper.ResourceGetter;
import codesquad.http.urlMapper.UrlResourceMap;

import java.io.IOException;
import java.util.Optional;

public class UserHandler {
    private final UserRepository userRepository;

    public UserHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void registration(HttpRequest request, HttpResponse response) throws IOException {
        try {
            // 세개 중 하나라도 없으면 회원가입 로직 skip
            String userId = Optional.ofNullable(request.getParameter("userId")).orElseThrow(IllegalArgumentException::new);
            String username = Optional.ofNullable(request.getParameter("name")).orElseThrow(IllegalArgumentException::new);
            String password = Optional.ofNullable(request.getParameter("password")).orElseThrow(IllegalArgumentException::new);

            User user = User.factoryMethod(userId, username, password);
            userRepository.save(user);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            response.setStatusCode(200);
            //todo : 나중에 model 과 같은 객체를 사용하게 될 경우 리팩토링 필요
            //todo : 회원가입 로직을 수행하면 redirect 를 하게 할까?
            String resourcePath = UrlResourceMap.getResourcePathByUri(request.getUrlPath());
            response.setBody(ResourceGetter.getResourceBytesByPath(resourcePath));
            response.setContentType(ResourceGetter.getContentTypeByPath(resourcePath));
        }
    }
}
