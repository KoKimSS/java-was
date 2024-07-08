package codesquad;

import codesquad.was.server.Server;
import java.io.IOException;

import static codesquad.was.server.ServerConfig.*;
import static codesquad.was.server.ServerConfig.THREAD_POOL_SIZE;


public class Main {

    public static void main(String[] args) throws IOException {
        Server server = new Server(THREAD_POOL_SIZE,PORT, BACKLOG);
        try {
            server.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
