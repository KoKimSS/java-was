package codesquad.http.Servlet;

import codesquad.Main;
import codesquad.http.log.Log;
import codesquad.http.request.HttpRequest;
import codesquad.http.request.HttpRequestParser;
import codesquad.http.urlMapper.ResourceMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static codesquad.http.log.Log.*;
import static codesquad.http.response.HttpResponseSender.*;
import static codesquad.http.response.HttpResponseSender.sendHttpResponse;
import static codesquad.http.urlMapper.ResourceGetter.getContentType;
import static codesquad.http.urlMapper.ResourceGetter.getResourceBytes;

public class Servlet {
    private static final Logger log = LoggerFactory.getLogger(Servlet.class);
    private final ResourceMapping resourceMapping;

    public Servlet(ResourceMapping resourceMapping) {
        this.resourceMapping = resourceMapping;
    }

    public void handleClientRequest(Socket clientSocket) throws IOException {

        InputStream inputStream = clientSocket.getInputStream();
        log(inputStream.toString());

        HttpRequest request = HttpRequestParser.parseHttpRequest(inputStream);
        log(request.toString());


        // HTTP 응답을 생성합니다.
        OutputStream clientOutput = clientSocket.getOutputStream();
        String resourcePath = "";

        String url = request.getUrl();
        if (url.startsWith("/img")) {
            resourcePath = "/static" + url;
        } else {
            resourcePath = resourceMapping.getResourcePath(url);
        }

        // 요청된 URL과 매핑된 리소스 파일 경로 가져오기
        addResponseToSocket(clientSocket, resourcePath, clientOutput);

        clientOutput.flush();
    }

    private static void addResponseToSocket(Socket clientSocket, String resourcePath, OutputStream clientOutput) throws IOException {
        if (resourcePath != null) {
            if (resourcePath.endsWith(".html")) {
                sendHtmlResponse(clientSocket.getOutputStream(), "200 OK", "text/html", getResourceBytes(resourcePath));
            } else if (resourcePath.endsWith(".css")) {
                sendHttpResponse(clientSocket.getOutputStream(), "200 OK", "text/css", getResourceBytes(resourcePath));
            } else if (resourcePath.endsWith(".js")) {
                sendHttpResponse(clientSocket.getOutputStream(), "200 OK", "application/javascript", getResourceBytes(resourcePath));
            } else if (resourcePath.endsWith(".jpg") || resourcePath.endsWith(".png") || resourcePath.endsWith(".svg")) {
                sendImageResponse(clientSocket.getOutputStream(), "200 OK", getContentType(resourcePath), getResourceBytes(resourcePath));
            }

            // HTTP 응답 보내기
            sendHttpResponse(clientOutput, "200 OK", getContentType(resourcePath), getResourceBytes(resourcePath));
        } else {
            // 요청된 리소스가 없음 (404 Not Found)
            sendHttpResponse(clientOutput, "404 Not Found", "text/plain", "Resource not found".getBytes(StandardCharsets.UTF_8));
        }
    }
}
