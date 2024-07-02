package codesquad;

import codesquad.http.webServer.WebServer;
import codesquad.http.log.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Main {
    private static final int THREAD_POOL_SIZE = 10;
    private static final int PORT = 8080;
    private static final WebServer WEB_SERVER = new WebServer();

    public static void main(String[] args) throws IOException {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        ServerSocket serverSocket = new ServerSocket(PORT);

        Log.log("Server started on port " + PORT);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            executorService.submit(() -> {
                try {
                    WEB_SERVER.handleClientRequest(clientSocket);
                    clientSocket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
