package codesquad;

import codesquad.http.Servlet.Servlet;
import codesquad.http.log.Log;
import codesquad.http.request.HttpRequest;
import codesquad.http.request.HttpRequestParser;
import codesquad.http.urlMapper.ResourceMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static codesquad.http.response.HttpResponseSender.*;
import static codesquad.http.urlMapper.ResourceGetter.*;


public class Main {
    private static final int THREAD_POOL_SIZE = 10;
    private static final int PORT = 8080;
    private static final ResourceMapping resourceMapping = new ResourceMapping();
    private static final Servlet servlet = new Servlet(resourceMapping);

    public static void main(String[] args) throws IOException {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        ServerSocket serverSocket = new ServerSocket(PORT);

        Log.log("Server started on port " + PORT);
        while (true) {
            Socket clientSocket = serverSocket.accept();
            executorService.submit(() -> {
                try {
                    servlet.handleClientRequest(clientSocket);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
