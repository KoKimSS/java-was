package codesquad.was.server;

import codesquad.was.util.ConsoleColors;
import codesquad.was.webServer.WebServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static codesquad.business.configuration.UrlPathResourceMapConfig.setUrlPathResourceMap;
import static codesquad.business.configuration.handlerMapConfig.setHandlerMap;
import static codesquad.was.util.ResourceGetter.getResourceBytesByPath;

public class Server {

    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    private final int port;
    private final WebServer webServer = new WebServer();
    private Selector selector;
    private ServerSocketChannel serverChannel;
    
    // 비즈니스 로직 처리용 스레드풀 (논블로킹 I/O + 스레드풀 하이브리드)
    private final ExecutorService businessThreadPool;

    public Server(int port) throws IOException {
        this.port = port;
        this.selector = Selector.open();
        this.serverChannel = ServerSocketChannel.open();
        this.serverChannel.configureBlocking(false);
        this.serverChannel.bind(new InetSocketAddress(port));
        this.serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        
        // CPU 코어 수의 2배 스레드풀 (I/O 집약적이므로)
        this.businessThreadPool = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors() * 2
        );
    }

    public void run() throws IOException {
        setUrlPathResourceMap();
        setHandlerMap();
        System.out.println(new String(getResourceBytesByPath("/banner.txt"), StandardCharsets.UTF_8));
        System.out.println(ConsoleColors.GREEN + "  :: SeungSu WAS (NIO + ThreadPool Hybrid) ::" + ConsoleColors.RESET + "                (v2.1.0)");
        logger.info("Hybrid NIO Server started on port {} with {} business threads", 
                   port, Runtime.getRuntime().availableProcessors() * 2);

        while (true) {
            try {
                // Non-blocking select with timeout
                int readyChannels = selector.select(100); // 100ms timeout
                if (readyChannels == 0) {
                    continue;
                }

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();

                    try {
                        if (key.isValid()) {
                            if (key.isAcceptable()) {
                                handleAccept(key);
                            } else if (key.isReadable()) {
                                handleRead(key);
                            } else if (key.isWritable()) {
                                handleWrite(key);
                            }
                        }
                    } catch (Exception e) {
                        logger.error("Error handling channel: {}", e.getMessage());
                        closeChannel(key);
                    }
                }
            } catch (IOException e) {
                logger.error("Selector error: {}", e.getMessage());
                break;
            }
        }
    }

    private void handleAccept(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverChannel.accept();
        
        if (clientChannel != null) {
            clientChannel.configureBlocking(false);
            clientChannel.register(selector, SelectionKey.OP_READ);
            logger.debug("New client connected: {}", clientChannel.getRemoteAddress());
        }
    }

    private void handleRead(SelectionKey key) {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        
        try {
            // 요청 데이터 읽기 (논블로킹)
            String requestData = webServer.readRequest(clientChannel);
            
            if (requestData != null) {
                // 비즈니스 로직을 별도 스레드에서 비동기 처리
                CompletableFuture.supplyAsync(() -> {
                    try {
                        return webServer.processRequest(requestData);
                    } catch (Exception e) {
                        logger.error("Error processing request: {}", e.getMessage());
                        return webServer.createErrorResponse(e);
                    }
                }, businessThreadPool).thenAccept(responseData -> {
                    // 응답 준비 완료시 WRITE 이벤트로 전환
                    try {
                        if (key.isValid() && clientChannel.isOpen()) {
                            key.attach(responseData);
                            key.interestOps(SelectionKey.OP_WRITE);
                            selector.wakeup(); // Selector를 깨워서 새로운 이벤트 처리
                        }
                    } catch (Exception e) {
                        logger.error("Error setting write operation: {}", e.getMessage());
                        closeChannel(key);
                    }
                });
                
                // 현재는 READ에서 제거 (비동기 처리중)
                key.interestOps(0);
                
            } else {
                // 아직 완전한 요청이 아님, 계속 READ 대기
                key.interestOps(SelectionKey.OP_READ);
            }
            
        } catch (Exception e) {
            logger.error("Error reading from client: {}", e.getMessage());
            closeChannel(key);
        }
    }

    private void handleWrite(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        byte[] responseData = (byte[]) key.attachment();
        
        if (responseData != null) {
            try {
                webServer.sendResponse(clientChannel, responseData);
                logger.debug("Response sent to client: {}", clientChannel.getRemoteAddress());
            } catch (IOException e) {
                logger.error("Error writing to client: {}", e.getMessage());
            }
        }
        
        // 응답 완료 후 연결 종료
        closeChannel(key);
    }

    private void closeChannel(SelectionKey key) {
        try {
            if (key.channel().isOpen()) {
                key.channel().close();
            }
            key.cancel();
        } catch (IOException e) {
            logger.error("Error closing channel: {}", e.getMessage());
        }
    }

    public void close() throws IOException {
        if (businessThreadPool != null) {
            businessThreadPool.shutdown();
        }
        if (selector != null && selector.isOpen()) {
            selector.close();
        }
        if (serverChannel != null && serverChannel.isOpen()) {
            serverChannel.close();
        }
    }
}
