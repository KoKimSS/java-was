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
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * AIO 기반 WebServer (Async Non-blocking)
 * - AsynchronousSocketChannel 사용
 * - 모든 I/O가 완전히 비동기
 * - 콜백 기반 처리
 */
public class AIOWebServer {

    private static final Logger logger = LoggerFactory.getLogger(AIOWebServer.class);
    private final DispatcherServlet dispatcherServlet;
    
    // 클라이언트별 요청 데이터 버퍼 저장 (AIO에서도 스트리밍 요청 처리 필요)
    private final Map<AsynchronousSocketChannel, ByteArrayOutputStream> requestBuffers = new ConcurrentHashMap<>();
    private static final int BUFFER_SIZE = 8192;

    public AIOWebServer() {
        this.dispatcherServlet = new DispatcherServlet();
    }

    /**
     * 비동기 요청 데이터 읽기 (Async Non-blocking)
     * @param clientChannel 클라이언트 소켓 채널
     * @param handler 완료 핸들러 (요청 문자열 또는 null)
     */
    public void readRequest(AsynchronousSocketChannel clientChannel, 
                           CompletionHandler<String, Void> handler) {
        
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        
        // 비동기 읽기 시작 (Async Non-blocking)
        clientChannel.read(buffer, null, new CompletionHandler<Integer, Void>() {
            @Override
            public void completed(Integer bytesRead, Void attachment) {
                if (bytesRead == -1) {
                    // 클라이언트가 연결을 닫음
                    requestBuffers.remove(clientChannel);
                    handler.completed(null, null);
                    return;
                }
                
                if (bytesRead == 0) {
                    // 데이터가 없음, 다시 읽기 시도
                    readRequest(clientChannel, handler);
                    return;
                }

                try {
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
                        // 완전한 요청이면 버퍼 정리하고 완료 알림
                        requestBuffers.remove(clientChannel);
                        handler.completed(requestData, null);
                    } else {
                        // 아직 완전한 요청이 아니면 더 읽기
                        readRequest(clientChannel, handler);
                    }
                    
                } catch (IOException e) {
                    logger.error("Error processing read data: {}", e.getMessage());
                    requestBuffers.remove(clientChannel);
                    handler.failed(e, null);
                }
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
                requestBuffers.remove(clientChannel);
                handler.failed(exc, null);
            }
        });
    }

    /**
     * 요청 처리 (동기 - 별도 스레드에서 실행)
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
     * 비동기 응답 전송 (Async Non-blocking)
     * @param clientChannel 클라이언트 소켓 채널
     * @param responseData 응답 데이터
     * @param handler 완료 핸들러 (전송된 바이트 수)
     */
    public void sendResponse(AsynchronousSocketChannel clientChannel, 
                           byte[] responseData,
                           CompletionHandler<Integer, Void> handler) {
        
        ByteBuffer buffer = ByteBuffer.wrap(responseData);
        
        // 비동기 쓰기 시작 (Async Non-blocking)
        writeBuffer(clientChannel, buffer, 0, handler);
    }

    /**
     * 버퍼 비동기 쓰기 (재귀적으로 전체 데이터 전송)
     */
    private void writeBuffer(AsynchronousSocketChannel clientChannel,
                           ByteBuffer buffer,
                           int totalWritten,
                           CompletionHandler<Integer, Void> handler) {
        
        clientChannel.write(buffer, null, new CompletionHandler<Integer, Void>() {
            @Override
            public void completed(Integer bytesWritten, Void attachment) {
                int newTotal = totalWritten + bytesWritten;
                
                if (buffer.hasRemaining()) {
                    // 아직 더 전송할 데이터가 있음
                    writeBuffer(clientChannel, buffer, newTotal, handler);
                } else {
                    // 전송 완료
                    handler.completed(newTotal, null);
                }
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
                handler.failed(exc, null);
            }
        });
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

    /**
     * 통계 정보
     */
    public int getActiveRequestBuffers() {
        return requestBuffers.size();
    }
}
