package codesquad.was.http.response;

import codesquad.was.http.common.HttpCookie;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class HttpResponseSender {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(HttpResponseSender.class);


    private HttpResponseSender() {
    }

    /**
     * HTTP 응답을 생성하고 클라이언트에게 보냅니다.
     *
     * @param outputStream 클라이언트의 OutputStream
     * @param response     응답으로 보낼 HttpResponse
     * @throws IOException 전송 중 발생할 수 있는 IO 예외
     */
    public static void sendHttpResponse(OutputStream outputStream, HttpResponse response) throws IOException {
        StringBuilder responseSB = new StringBuilder();
        String statusLine = "HTTP/1.1 " + response.getStatusCode().getCode() + "\r\n";
        byte[] body = response.getBody();
        StringBuilder headers = new StringBuilder("Content-Type: " + response.getContentType() + "\r\n"
                + "Content-Length: " + (body == null ? 0 : body.length) + "\r\n");
        for (String key : response.getHeaders().getHeaderNames()) {
            List<String> values = response.getHeaders().getHeader(key);
            for (String value : values) {
                headers.append(key).append(": ").append(value).append("\r\n");
            }
        }

        Map<String, HttpCookie> cookies = response.getCookies();
        cookies.forEach((key, cookie) -> headers.append("Set-Cookie: ").append(cookie.toString()).append("\r\n"));
        headers.append("\r\n");

        outputStream.write(statusLine.getBytes(StandardCharsets.UTF_8));
        outputStream.write(headers.toString().getBytes(StandardCharsets.UTF_8));
        if (body != null) {
            outputStream.write(body);
        }
        responseSB.append("\n").append(statusLine).append(headers).append("\n");
        logger.info(responseSB.toString());
    }

}

