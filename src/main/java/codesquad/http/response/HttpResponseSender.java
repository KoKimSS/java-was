package codesquad.http.response;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HttpResponseSender {


    /**
     * HTTP 응답을 생성하고 클라이언트에게 보냅니다.
     *
     * @param outputStream 클라이언트의 OutputStream
     * @param status       HTTP 상태 코드 및 메시지
     * @param contentType  Content-Type 헤더 값
     * @param content      응답 본문의 바이트 배열
     * @throws IOException 전송 중 발생할 수 있는 IO 예외
     */
    public static void sendHttpResponse(OutputStream outputStream, String status, String contentType, byte[] content) throws IOException {
        String statusLine = "HTTP/1.1 " + status + "\r\n";
        String headers = "Content-Type: " + contentType + "\r\n"
                + "Content-Length: " + content.length + "\r\n"
                + "\r\n";

        outputStream.write(statusLine.getBytes(StandardCharsets.UTF_8));
        outputStream.write(headers.getBytes(StandardCharsets.UTF_8));
        outputStream.write(content);
        outputStream.flush();
    }

}

