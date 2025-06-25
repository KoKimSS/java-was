package codesquad.was.http.response;

import codesquad.was.http.common.HttpCookie;
import codesquad.was.http.common.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpResponseSender {

    private static final Logger logger = LoggerFactory.getLogger(HttpResponseSender.class);

    /**
     * OutputStream에 HTTP 응답 전송 (기존 메서드 유지)
     */
    public static void sendHttpResponse(OutputStream outputStream, HttpResponse response) throws IOException {
        byte[] responseBytes = getHttpResponseBytes(response);
        outputStream.write(responseBytes);
        outputStream.flush();
    }

    /**
     * HTTP 응답을 바이트 배열로 변환 (NIO용 새로운 메서드)
     */
    public static byte[] getHttpResponseBytes(HttpResponse response) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        // Status Line
        String statusMessage = response.getStatusMessage();
        String statusLine = String.format("HTTP/1.1 %d %s\r\n",
            response.getStatusCode().getCode(), 
            statusMessage);
        outputStream.write(statusLine.getBytes(StandardCharsets.UTF_8));

        // Content-Type 헤더
        if (response.getContentType() != null) {
            String contentTypeHeader = "Content-Type: " + response.getContentType() + "\r\n";
            outputStream.write(contentTypeHeader.getBytes(StandardCharsets.UTF_8));
        }

        // Content-Length 헤더
        if (response.getBody() != null) {
            String contentLengthHeader = "Content-Length: " + response.getBody().length + "\r\n";
            outputStream.write(contentLengthHeader.getBytes(StandardCharsets.UTF_8));
        } else {
            outputStream.write("Content-Length: 0\r\n".getBytes(StandardCharsets.UTF_8));
        }

        // 추가 헤더들
        HttpHeaders headers = response.getHeaders();
        for (String headerName : headers.getHeaderNames()) {
            String headerValue = headers.getValue(headerName);
            if (headerValue != null) {
                String header = headerName + ": " + headerValue + "\r\n";
                outputStream.write(header.getBytes(StandardCharsets.UTF_8));
            }
        }

        // 쿠키 헤더들
        Map<String, HttpCookie> cookies = response.getCookies();
        for (HttpCookie cookie : cookies.values()) {
            String cookieHeader = "Set-Cookie: " + cookie.toString() + "\r\n";
            outputStream.write(cookieHeader.getBytes(StandardCharsets.UTF_8));
        }

        // Connection 헤더 (NIO에서는 일반적으로 keep-alive 사용하지 않음)
        outputStream.write("Connection: close\r\n".getBytes(StandardCharsets.UTF_8));

        // 헤더 종료
        outputStream.write("\r\n".getBytes(StandardCharsets.UTF_8));

        // Body
        if (response.getBody() != null && response.getBody().length > 0) {
            outputStream.write(response.getBody());
        }

        byte[] responseBytes = outputStream.toByteArray();
        outputStream.close();
        
        return responseBytes;
    }
}
