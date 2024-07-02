package codesquad.http.handler;

import codesquad.http.User.UserRepository;
import codesquad.http.request.HttpRequest;
import codesquad.http.response.HttpResponse;

public class Handler {

    private final UserHandler userHandler;

    public Handler(UserHandler userHandler) {
        this.userHandler = userHandler;
    }

    public void handlerMapping(HttpRequest request) {
        System.out.println(request.getUri()+" "+request.getMethod());
        if(request.getMethod().equals("GET") && request.getUri().equals("/registration")) {
            userHandler.saveUser(request);
        }
    }
}
