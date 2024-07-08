package codesquad.was.handler;

import codesquad.was.response.HTTPStatusCode;
import codesquad.was.util.ResourceGetter;
import codesquad.was.util.UrlPathResourceMap;
import codesquad.was.exception.InternalServerException;
import codesquad.was.user.User;
import codesquad.was.user.UserRepository;
import codesquad.was.request.HttpRequest;
import codesquad.was.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static codesquad.was.util.ResourceGetter.readBytesFromFile;

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
        } catch (IllegalArgumentException e) {
            log.debug("Invalid username or password or userId: {}", e.getMessage());
        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        } finally {
            // 처리 완료 후 리디렉션
            response.setStatusCode(HTTPStatusCode.FOUND); // 302 상태 코드 설정
            response.setHeader("Location", "/index.html");
        }
    }

    @Override
    public HttpResponse handleRequest(HttpRequest request) throws InternalServerException, IOException {
        HttpResponse response = new HttpResponse();
        registration(request,response);
        return response;
    }
}
