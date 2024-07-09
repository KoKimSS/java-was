package codesquad.was.handler;

import codesquad.was.common.HttpMethod;
import codesquad.was.exception.MethodNotAllowedException;
import codesquad.was.common.HTTPStatusCode;
import codesquad.was.exception.InternalServerException;
import codesquad.was.user.User;
import codesquad.was.user.UserRepository;
import codesquad.was.request.HttpRequest;
import codesquad.was.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class UserHandler implements Handler {
    private static final Logger log = LoggerFactory.getLogger(UserHandler.class);
    private final UserRepository userRepository;

    public UserHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void registration(HttpRequest request, HttpResponse response) throws InternalServerException{
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
            HttpResponse.setRedirect(response,HTTPStatusCode.FOUND,"/index.html");
        }
    }

    @Override
    public HttpResponse handleRequest(HttpRequest request) throws InternalServerException, MethodNotAllowedException {
        return doBusinessByMethod(request);
    }

    private HttpResponse doBusinessByMethod(HttpRequest request) throws InternalServerException, MethodNotAllowedException {
        HttpResponse response = new HttpResponse();

        //todo : 무조건 HttpMethod 중 하나로 매칭이 되게 해야한다
        // 검증을 어떻게 해야 할까?

        if(request.getMethod().equals(HttpMethod.POST)){
            registration(request, response);
            return response;
        }

        throw new MethodNotAllowedException();
    }
}
