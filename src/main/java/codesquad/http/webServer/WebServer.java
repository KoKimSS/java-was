package codesquad.http.webServer;

import codesquad.http.User.UserRepository;
import codesquad.http.handler.Handler;
import codesquad.http.handler.UserHandler;
import codesquad.http.request.HttpRequest;
import codesquad.http.request.HttpRequestParser;
import codesquad.http.response.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static codesquad.http.log.Log.*;
import static codesquad.http.response.HttpResponseSender.sendHttpResponse;

/**
 * Socket 통신을 Http 로 변환하여 통신하게 해준다
 * HttpRequest, HttpResponse 생성 및 반환
 */
public class WebServer {
    private final Handler handler;

    public WebServer() {
        UserRepository userRepository = new UserRepository();
        UserHandler userHandler = new UserHandler(userRepository);
        this.handler = new Handler(userHandler);
    }

    public void handleClientRequest(Socket clientSocket) throws IOException {
        InputStream inputStream = clientSocket.getInputStream();

        // HttpRequest 생성
        HttpRequest request = HttpRequestParser.parseHttpRequest(inputStream);
        log(request.toString());

        // 비즈니스 로직 수행 후 HttpResponse 생성
        HttpResponse response = handler.handlerMapping(request);
        log(response.toString());
        OutputStream clientOutput = clientSocket.getOutputStream();

        // 요청된 URL과 매핑된 리소스 파일 경로 가져오기
        sendHttpResponse(clientOutput, response);
        clientOutput.flush();
    }

}
