package codesquad.was.server;

import codesquad.was.exception.InternalServerException;
import codesquad.was.log.Log;
import codesquad.was.webServer.WebServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Server {

    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    private final int port;
    private final WebServer webServer = new WebServer();
    private final ExecutorService executorService;
    private final ServerSocket serverSocket;


    public Server(int threadPoolSize, int port, int backlog) throws IOException {
        this.port = port;
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
        //ThreadPoolExecutor -> 미리 쓰레드를 10개 생성해 놓을 수 있음
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executorService;
        this.serverSocket = new ServerSocket(port, backlog);
    }

    public void run() throws IOException {

        logger.info("Server started on port {}", port);

        while (true) {
            // Queue 연결 하기 전 (   Queue 50
            Socket clientSocket = serverSocket.accept();
            executorService.execute(() -> {
                try {
                    webServer.handleClientRequest(clientSocket);
                    OutputStream clientOutput = clientSocket.getOutputStream();
                    clientOutput.flush();
                    clientOutput.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

//            executorService.submit(() -> {
//                try (Socket clientSocket = serverSocket.accept()) {
//                    webServer.handleClientRequest(clientSocket);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (InternalServerException e) {
//                    throw new RuntimeException(e);
//                }
//            });
        }
    }

    public void close() throws IOException {
        serverSocket.close();
    }
}
