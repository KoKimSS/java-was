package codesquad.was.webServer;

import codesquad.was.http.common.HttpStatusCode;
import codesquad.was.dispatcherServlet.DispatcherServlet;
import codesquad.was.exception.CommonException;
import codesquad.was.http.request.HttpRequest;
import codesquad.was.http.request.HttpRequestParser;
import codesquad.was.http.response.HttpResponse;
import codesquad.was.http.response.HttpResponseSender;

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
            response.setStatusCode(HttpStatusCode.BAD_REQUEST);
            HttpResponseSender.sendHttpResponse(clientOutput, response);
            return;
        }

        HttpResponse response = null;
        try {
            // 비즈니스 로직 수행 후 HttpResponse 생성
            response = dispatcherServlet.callHandler(request);
        } catch (CommonException e) {
            e.printStackTrace();
            response = new HttpResponse();
            response.setStatusCode(e.getHttpStatusCode());
            response.setStatusMessage(e.getMessage());
        }  catch (Exception e) {
            e.printStackTrace();
            response.setStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR);
            response.setStatusMessage(e.getMessage());
        }

        // outPutStream 에 HttpResponse 추가 !
        HttpResponseSender.sendHttpResponse(clientOutput, response);

        inputStream.close();
        clientOutput.flush();
        clientOutput.close();
    }
}
