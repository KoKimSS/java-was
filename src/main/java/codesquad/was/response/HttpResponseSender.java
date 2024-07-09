package codesquad.was.response;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class HttpResponseSender {

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
        String statusLine = "HTTP/1.1 " + response.getStatusCode().getCode() + "\r\n";
        byte[] body = response.getBody();
        StringBuilder headers = new StringBuilder("Content-Type: " + response.getContentType() + "\r\n"
                + "Content-Length: " + (body == null ? 0 : body.length) + "\r\n");

        for(String key : response.getHeaders().keySet()) {

            headers.append(key).append(": ").append(response.getHeaders().get(key)).append("\r\n");
        }

        headers.append("\r\n");

        outputStream.write(statusLine.getBytes(StandardCharsets.UTF_8));
        outputStream.write(headers.toString().getBytes(StandardCharsets.UTF_8));
        if(body != null) {
            outputStream.write(body);
        }
    }

}

