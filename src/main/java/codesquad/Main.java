package codesquad;

import codesquad.was.server.AIOServer;
import codesquad.was.server.Server;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        // 서버 모드 선택 (시스템 프로퍼티로 설정 가능)
        String serverMode = System.getProperty("server.mode", "aio"); // 기본값: aio
        
        if ("nio".equals(serverMode)) {
            runNIOServer();
        } else {
            runAIOServer();
        }
    }
    
    /**
     * NIO 서버 실행 (I/O Multiplexing - Async Blocking)
     */
    private static void runNIOServer() throws IOException {
        System.out.println("Starting NIO Server (I/O Multiplexing)...");
        Server server = new Server(8080);

        try {
            server.run();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.close();
        }
    }
    
    /**
     * AIO 서버 실행 (Async Non-blocking)
     */
    private static void runAIOServer() throws IOException, InterruptedException {
        System.out.println("Starting AIO Server (Async Non-blocking)...");
        AIOServer server = new AIOServer(8080);

        // 종료 훅 등록
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                System.out.println("\nShutting down AIO server...");
                server.close();
            } catch (IOException e) {
                System.err.println("Error during shutdown: " + e.getMessage());
            }
        }));

        try {
            server.run();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.close();
        }
    }
}
