package codesquad.was.server;

import codesquad.was.util.ConsoleColors;
import codesquad.was.webServer.AIOWebServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import static codesquad.business.configuration.UrlPathResourceMapConfig.setUrlPathResourceMap;
import static codesquad.business.configuration.handlerMapConfig.setHandlerMap;
import static codesquad.was.util.ResourceGetter.getResourceBytesByPath;

/**
 * AIO 기반 서버 (Async Non-blocking)
 * - AsynchronousServerSocketChannel 사용
 * - 모든 I/O가 완전히 비동기
 * - 콜백 기반 처리
 */
public class AIOServer {

    private static final Logger logger = LoggerFactory.getLogger(AIOServer.class);
    private final int port;
    private final AIOWebServer webServer;
    private AsynchronousServerSocketChannel serverChannel;
    private final ExecutorService executorService;
    private final CountDownLatch shutdownLatch = new CountDownLatch(1);
    private final AtomicLong connectionCount = new AtomicLong(0);
    private volatile boolean running = true;

    public AIOServer(int port) throws IOException {
        this.port = port;
        this.webServer = new AIOWebServer();
        
        // AIO용 스레드풀 (I/O 스레드 + 비즈니스 로직 스레드)
        this.executorService = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors() * 2
        );
        
        // 비동기 서버 소켓 채널 생성
        this.serverChannel = AsynchronousServerSocketChannel.open()
            .bind(new InetSocketAddress(port));
    }

    public void run() throws IOException, InterruptedException {
        setUrlPathResourceMap();
        setHandlerMap();
        
        System.out.println(new String(getResourceBytesByPath("/banner.txt"), StandardCharsets.UTF_8));
        System.out.println(ConsoleColors.GREEN + "  :: SeungSu WAS (AIO Async Non-blocking) ::" + ConsoleColors.RESET + "                (v3.0.0)");
        logger.info("AIO Server started on port {} with {} threads", 
                   port, Runtime.getRuntime().availableProcessors() * 2);

        // 비동기 연결 수락 시작
        startAcceptingConnections();
        
        // 서버 종료까지 대기
        shutdownLatch.await();
        logger.info("AIO Server stopped");
    }

    /**
     * 비동기 연결 수락 시작
     */
    private void startAcceptingConnections() {
        if (!running) return;
        
        // 비동기 연결 수락 (Async Non-blocking)
        serverChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
            @Override
            public void completed(AsynchronousSocketChannel clientChannel, Void attachment) {
                // 성공적으로 연결 수락됨
                long connId = connectionCount.incrementAndGet();
                logger.debug("New client connected: {} (connection #{})", 
                           getRemoteAddress(clientChannel), connId);

                // 다음 연결을 위해 재귀적으로 accept 호출
                startAcceptingConnections();

                // 클라이언트 요청 처리 시작
                handleClient(clientChannel, connId);
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
                if (running) {
                    logger.error("Failed to accept connection: {}", exc.getMessage());
                    // 실패해도 다시 연결 수락 시도
                    startAcceptingConnections();
                }
            }
        });
    }

    /**
     * 클라이언트 요청 처리 (완전 비동기)
     */
    private void handleClient(AsynchronousSocketChannel clientChannel, long connectionId) {
        // 비동기 요청 읽기 시작
        webServer.readRequest(clientChannel, new CompletionHandler<String, Void>() {
            @Override
            public void completed(String requestData, Void attachment) {
                if (requestData != null) {
                    logger.debug("Request received from connection #{}: {} bytes", 
                               connectionId, requestData.length());

                    // 비즈니스 로직을 별도 스레드에서 비동기 처리
                    executorService.submit(() -> {
                        try {
                            byte[] responseData = webServer.processRequest(requestData);
                            
                            // 비동기 응답 전송
                            webServer.sendResponse(clientChannel, responseData, 
                                new CompletionHandler<Integer, Void>() {
                                    @Override
                                    public void completed(Integer bytesWritten, Void attachment) {
                                        logger.debug("Response sent to connection #{}: {} bytes", 
                                                   connectionId, bytesWritten);
                                        closeClient(clientChannel, connectionId);
                                    }

                                    @Override
                                    public void failed(Throwable exc, Void attachment) {
                                        logger.error("Failed to send response to connection #{}: {}", 
                                                   connectionId, exc.getMessage());
                                        closeClient(clientChannel, connectionId);
                                    }
                                });
                                
                        } catch (Exception e) {
                            logger.error("Error processing request for connection #{}: {}", 
                                       connectionId, e.getMessage());
                            
                            // 에러 응답 전송
                            byte[] errorResponse = webServer.createErrorResponse(e);
                            webServer.sendResponse(clientChannel, errorResponse, 
                                new CompletionHandler<Integer, Void>() {
                                    @Override
                                    public void completed(Integer bytesWritten, Void attachment) {
                                        closeClient(clientChannel, connectionId);
                                    }

                                    @Override
                                    public void failed(Throwable exc, Void attachment) {
                                        closeClient(clientChannel, connectionId);
                                    }
                                });
                        }
                    });
                } else {
                    // 요청 데이터가 없으면 연결 종료
                    closeClient(clientChannel, connectionId);
                }
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
                logger.error("Failed to read request from connection #{}: {}", 
                           connectionId, exc.getMessage());
                closeClient(clientChannel, connectionId);
            }
        });
    }

    /**
     * 클라이언트 연결 종료
     */
    private void closeClient(AsynchronousSocketChannel clientChannel, long connectionId) {
        try {
            if (clientChannel.isOpen()) {
                clientChannel.close();
                logger.debug("Client connection #{} closed", connectionId);
            }
        } catch (IOException e) {
            logger.error("Error closing client connection #{}: {}", connectionId, e.getMessage());
        }
    }

    /**
     * 원격 주소 가져오기 (예외 처리)
     */
    private String getRemoteAddress(AsynchronousSocketChannel channel) {
        try {
            return channel.getRemoteAddress().toString();
        } catch (IOException e) {
            return "unknown";
        }
    }

    /**
     * 서버 종료
     */
    public void close() throws IOException {
        running = false;
        
        if (executorService != null) {
            executorService.shutdown();
            logger.info("Thread pool shutdown initiated");
        }
        
        if (serverChannel != null && serverChannel.isOpen()) {
            serverChannel.close();
            logger.info("Server channel closed");
        }
        
        shutdownLatch.countDown();
    }

    /**
     * 통계 정보 출력
     */
    public void printStats() {
        logger.info("Current active connections: {}", connectionCount.get());
    }
}
