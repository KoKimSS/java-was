package codesquad.was.server;

import codesquad.business.repository.JdbcTemplate;
import codesquad.was.util.ConsoleColors;
import codesquad.was.webServer.WebServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static codesquad.business.configuration.UrlPathResourceMapConfig.setUrlPathResourceMap;
import static codesquad.business.configuration.handlerMapConfig.setHandlerMap;
import static codesquad.was.util.ResourceGetter.getResourceBytesByPath;

public class Server {

    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    private final int port;
    private final WebServer webServer = new WebServer();
    private final ExecutorService executorService;
    private final ServerSocket serverSocket;


    public Server(int threadPoolSize, int port, int backlog) throws IOException {
        this.port = port;
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
        this.serverSocket = new ServerSocket(port, backlog);
    }

    public void run() throws IOException {

        setUrlPathResourceMap();
        setHandlerMap();
        System.out.println(new String(getResourceBytesByPath("/banner.txt"), StandardCharsets.UTF_8));
        System.out.println(ConsoleColors.GREEN + "  :: SeungSu WAS ::"+ConsoleColors.RESET+"                (v1.0.0)");
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
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public void close() throws IOException {
        serverSocket.close();
    }
}
