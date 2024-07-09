package codesquad.was.webServer;

import codesquad.was.common.HTTPStatusCode;
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
        this.dispatcherServlet = new DispatcherServlet();
    }

    public void handleClientRequest(Socket clientSocket) throws IOException {
        InputStream inputStream = clientSocket.getInputStream();
        OutputStream clientOutput = clientSocket.getOutputStream();

        HttpRequest request = null;
        // HttpRequest 생성
        try {
            request = HttpRequestParser.parseHttpRequest(inputStream);
        } catch (IOException e) {
            // 파싱이 불가능 할 때 return BAD_REQUEST
            e.printStackTrace();
            HttpResponse response = new HttpResponse();
            response.setStatusCode(HTTPStatusCode.BAD_REQUEST);
            HttpResponseSender.sendHttpResponse(clientOutput, response);
            return;
        }

        Log.log(request.toString());


        // 비즈니스 로직 수행 후 HttpResponse 생성
        HttpResponse response = null;
        try {
            response = dispatcherServlet.callHandler(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.log(response.toString());

        // outPutStream 에 HttpResponse 추가 !
        HttpResponseSender.sendHttpResponse(clientOutput, response);
    }
}
