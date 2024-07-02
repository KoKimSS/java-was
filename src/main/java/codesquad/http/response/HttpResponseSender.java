package codesquad.http.response;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HttpResponseSender {


    /**
     * HTTP 응답을 생성하고 클라이언트에게 보냅니다.
     *
     * @param outputStream 클라이언트의 OutputStream
     * @param response     응답으로 보낼 HttpResponse
     * @throws IOException 전송 중 발생할 수 있는 IO 예외
     */
    public static void sendHttpResponse(OutputStream outputStream, HttpResponse response) throws IOException {
        String statusLine = "HTTP/1.1 " + response.getStatusCode() + "\r\n";
        byte[] body = response.getBody();
        String headers = "Content-Type: " + response.getContentType() + "\r\n"
                + "Content-Length: " + body.length + "\r\n"
                + "\r\n";

        outputStream.write(statusLine.getBytes(StandardCharsets.UTF_8));
        outputStream.write(headers.getBytes(StandardCharsets.UTF_8));
        outputStream.write(body);
        outputStream.flush();
    }

}

