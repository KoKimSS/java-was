package codesquad.http.handler;

import codesquad.http.User.User;
import codesquad.http.User.UserRepository;
import codesquad.http.request.HttpRequest;
import codesquad.http.response.HttpResponse;
import codesquad.http.urlMapper.ResourceGetter;
import codesquad.http.urlMapper.UrlResourceMap;

import java.io.IOException;

public class UserHandler {
    private final UserRepository userRepository;

    public UserHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void saveUser(HttpRequest request, HttpResponse response) throws IOException {
        String userId = request.getParameter("userId");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        User user = User.factoryMethod(userId, username, password);
        userRepository.save(user);


        response.setStatusCode(200);
        //todo : 나중에 model 과 같은 객체를 사용하게 될 경우 리팩토링 필요
        String resourcePath = UrlResourceMap.getResourcePathByUrl(request.getUrl());
        response.setBody(ResourceGetter.getResourceBytes(resourcePath));
        response.setContentType(ResourceGetter.getContentTypeByPath(resourcePath));
    }
}
