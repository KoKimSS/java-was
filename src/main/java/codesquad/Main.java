package codesquad;

import codesquad.http.server.Server;
import java.io.IOException;

import static codesquad.http.server.ServerConfig.*;
import static codesquad.http.server.ServerConfig.THREAD_POOL_SIZE;


public class Main {

    public static void main(String[] args) throws IOException {
        Server server = new Server(THREAD_POOL_SIZE,PORT, BACKLOG);
        server.run();
    }
}
