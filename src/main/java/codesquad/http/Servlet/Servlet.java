package codesquad.http.Servlet;

import codesquad.Main;
import codesquad.http.log.Log;
import codesquad.http.request.HttpRequest;
import codesquad.http.request.HttpRequestParser;
import codesquad.http.response.HttpResponse;
import codesquad.http.urlMapper.ResourceMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static codesquad.http.log.Log.*;
import static codesquad.http.response.HttpResponseSender.sendHttpResponse;
import static codesquad.http.urlMapper.ResourceGetter.getContentType;
import static codesquad.http.urlMapper.ResourceGetter.getResourceBytes;

public class Servlet {
    private final ResourceMapping resourceMapping;

    public Servlet(ResourceMapping resourceMapping) {
        this.resourceMapping = resourceMapping;
    }

    public void handleClientRequest(Socket clientSocket) throws IOException {
        InputStream inputStream = clientSocket.getInputStream();

        HttpRequest request = HttpRequestParser.parseHttpRequest(inputStream);
        log(request.toString());

        // HTTP 응답을 생성합니다.
        OutputStream clientOutput = clientSocket.getOutputStream();
        String resourcePath = "";

        resourcePath = resourceMapping.getResourcePath(request.getUrl());

        // 요청된 URL과 매핑된 리소스 파일 경로 가져오기
        addResponseToSocket(resourcePath, clientOutput);

        clientOutput.flush();
    }

    private static void addResponseToSocket(String resourcePath, OutputStream clientOutput) throws IOException {
        if (resourcePath != null) {
            byte[] resourceBytes = getResourceBytes(resourcePath);
            // HTTP 응답 보내기
            sendHttpResponse(clientOutput, "200 OK", getContentType(resourcePath), resourceBytes);
        } else {
            // 요청된 리소스가 없음 (404 Not Found)
            sendHttpResponse(clientOutput, "404 Not Found", "text/plain", "Resource not found".getBytes(StandardCharsets.UTF_8));
        }
    }
}
