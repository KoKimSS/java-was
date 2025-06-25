package codesquad.was.webServer;

import codesquad.was.http.common.HttpStatusCode;
import codesquad.was.dispatcherServlet.DispatcherServlet;
import codesquad.was.exception.CommonException;
import codesquad.was.http.request.HttpRequest;
import codesquad.was.http.request.HttpRequestParser;
import codesquad.was.http.response.HttpResponse;
import codesquad.was.http.response.HttpResponseSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * NIO 기반 WebServer (하이브리드 아키텍처)
 * - NIO로 요청 수신 (논블로킹)
 * - 비즈니스 로직은 별도 스레드풀에서 처리
 */
public class WebServer {

    private static final Logger logger = LoggerFactory.getLogger(WebServer.class);
    private final DispatcherServlet dispatcherServlet;
    
    // 클라이언트별 요청 데이터 버퍼 저장
    private final Map<SocketChannel, ByteArrayOutputStream> requestBuffers = new ConcurrentHashMap<>();
    private static final int BUFFER_SIZE = 8192;

    public WebServer() {
        this.dispatcherServlet = new DispatcherServlet();
    }

    /**
     * 요청 데이터 읽기 (논블로킹)
     * @param clientChannel 클라이언트 소켓 채널
     * @return 완전한 요청이 수신되면 요청 문자열, 아니면 null
     */
    public String readRequest(SocketChannel clientChannel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        
        // 채널에서 데이터 읽기 (논블로킹)
        int bytesRead = clientChannel.read(buffer);
        
        if (bytesRead == -1) {
            // 클라이언트가 연결을 닫음
            requestBuffers.remove(clientChannel);
            return null;
        }
        
        if (bytesRead == 0) {
            // 더 읽을 데이터가 없음
            return null;
        }

        // 읽은 데이터를 요청 버퍼에 추가
        ByteArrayOutputStream requestBuffer = requestBuffers.computeIfAbsent(
            clientChannel, k -> new ByteArrayOutputStream()
        );
        
        buffer.flip();
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        requestBuffer.write(data);

        // HTTP 요청이 완료되었는지 확인
        String requestData = requestBuffer.toString();
        if (isCompleteHttpRequest(requestData)) {
            // 완전한 요청이면 버퍼 정리하고 반환
            requestBuffers.remove(clientChannel);
            return requestData;
        }

        return null; // 아직 완전한 요청이 아님
    }

    /**
     * 요청 처리 (별도 스레드에서 실행)
     * @param requestData HTTP 요청 문자열
     * @return 응답 바이트 배열
     */
    public byte[] processRequest(String requestData) {
        try {
            HttpRequest request = HttpRequestParser.parseHttpRequest(requestData);
            HttpResponse response = dispatcherServlet.callHandler(request);
            return HttpResponseSender.getHttpResponseBytes(response);
            
        } catch (CommonException e) {
            logger.error("Business logic error: {}", e.getMessage());
            HttpResponse response = new HttpResponse();
            response.setStatusCode(e.getHttpStatusCode());
            response.setStatusMessage(e.getMessage());
            return safeGetResponseBytes(response);
            
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage());
            HttpResponse response = new HttpResponse();
            response.setStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR);
            response.setStatusMessage(e.getMessage());
            return safeGetResponseBytes(response);
        }
    }

    /**
     * 에러 응답 생성
     */
    public byte[] createErrorResponse(Throwable error) {
        HttpResponse response = new HttpResponse();
        response.setStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR);
        response.setStatusMessage("Server Error: " + error.getMessage());
        return safeGetResponseBytes(response);
    }

    /**
     * 응답 데이터를 클라이언트에게 전송
     */
    public void sendResponse(SocketChannel clientChannel, byte[] responseData) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(responseData);
        
        while (buffer.hasRemaining()) {
            int written = clientChannel.write(buffer);
            if (written == 0) {
                // 소켓 버퍼가 가득 참, 나중에 다시 시도해야 함
                logger.warn("Socket buffer full, response may be incomplete");
                break;
            }
        }
    }

    /**
     * 기존 handleClientRequest 메서드 (하위 호환성)
     */
    public byte[] handleClientRequest(SocketChannel clientChannel) throws IOException {
        String requestData = readRequest(clientChannel);
        if (requestData != null) {
            return processRequest(requestData);
        }
        return null;
    }

    /**
     * HTTP 요청이 완료되었는지 확인
     */
    private boolean isCompleteHttpRequest(String requestData) {
        if (!requestData.contains("\r\n\r\n")) {
            return false;
        }

        // Content-Length가 있으면 바디까지 확인
        String[] lines = requestData.split("\r\n");
        int contentLength = 0;
        
        for (String line : lines) {
            if (line.toLowerCase().startsWith("content-length:")) {
                try {
                    contentLength = Integer.parseInt(line.split(":")[1].trim());
                    break;
                } catch (NumberFormatException e) {
                    return true; // Content-Length 파싱 실패시 헤더만 확인
                }
            }
        }

        if (contentLength > 0) {
            int headerEndIndex = requestData.indexOf("\r\n\r\n") + 4;
            int bodyLength = requestData.length() - headerEndIndex;
            return bodyLength >= contentLength;
        }

        return true;
    }

    /**
     * 안전한 응답 바이트 변환 (예외 발생시 기본 응답 반환)
     */
    private byte[] safeGetResponseBytes(HttpResponse response) {
        try {
            return HttpResponseSender.getHttpResponseBytes(response);
        } catch (IOException e) {
            logger.error("Failed to create response bytes: {}", e.getMessage());
            // 최소한의 HTTP 응답 반환
            String errorResponse = "HTTP/1.1 500 Internal Server Error\r\n" +
                                 "Content-Length: 13\r\n" +
                                 "Connection: close\r\n\r\n" +
                                 "Server Error";
            return errorResponse.getBytes();
        }
    }
}
