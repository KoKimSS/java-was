package codesquad.was.server;

import codesquad.was.exception.InternalServerException;
import codesquad.was.log.Log;
import codesquad.was.webServer.WebServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private final int port;
    private final WebServer webServer = new WebServer();
    private final ExecutorService executorService;
    private final ServerSocket serverSocket;

    public Server(int threadPoolSize, int port, int backlog) throws IOException {
        this.port = port;
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
        this.serverSocket = new ServerSocket(port,backlog);
    }

    public void run() throws IOException {

        Log.log("Server started on port " + port);

        while (true) {
            // Queue 연결 하기 전 (   Queue 50

            executorService.submit(() -> {
                try (Socket clientSocket = serverSocket.accept()) {
                    webServer.handleClientRequest(clientSocket);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InternalServerException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public void close() throws IOException {
        serverSocket.close();
    }
}
