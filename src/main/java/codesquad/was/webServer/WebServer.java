package codesquad.was.webServer;

import codesquad.was.dispatcherServlet.DispatcherServlet;
import codesquad.was.handler.UserHandler;
import codesquad.was.log.Log;
import codesquad.was.request.HttpRequest;
import codesquad.was.request.HttpRequestParser;
import codesquad.was.response.HttpResponse;
import codesquad.was.response.HttpResponseSender;
import codesquad.was.user.UserRepository;
import codesquad.was.exception.InternalServerException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Socket 통신을 Http 로 변환하여 통신하게 해준다
 * HttpRequest, HttpResponse 생성 및 반환
 */

public class WebServer {

    private final DispatcherServlet dispatcherServlet;
    public WebServer() {
        UserRepository userRepository = new UserRepository();
        UserHandler userHandler = new UserHandler(userRepository);
        this.dispatcherServlet = new DispatcherServlet();
    }

    public void handleClientRequest(Socket clientSocket) throws IOException, InternalServerException {
        InputStream inputStream = clientSocket.getInputStream();

        // HttpRequest 생성
        HttpRequest request = HttpRequestParser.parseHttpRequest(inputStream);
        Log.log(request.toString());

        // 비즈니스 로직 수행 후 HttpResponse 생성
        HttpResponse response = dispatcherServlet.callHandler(request);
        Log.log(response.toString());
        OutputStream clientOutput = clientSocket.getOutputStream();

        // 요청된 URL과 매핑된 리소스 파일 경로 가져오기
        HttpResponseSender.sendHttpResponse(clientOutput, response);
        clientOutput.flush();
        clientOutput.close();
    }

}
