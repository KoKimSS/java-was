package codesquad.http.handler;

import codesquad.http.User.User;
import codesquad.http.User.UserRepository;
import codesquad.http.request.HttpRequest;

public class UserHandler {
    private final UserRepository userRepository;

    public UserHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void saveUser(HttpRequest request) {
        String userId = request.getParameter("userId");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        User user = User.factoryMethod(userId, username, password);
        userRepository.save(user);
    }
}
