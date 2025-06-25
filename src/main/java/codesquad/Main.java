package codesquad;

import codesquad.was.server.Server;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        // NIO 서버는 ThreadPool 없이 단일 스레드에서 multiplexing 처리
        Server server = new Server(8080);

        try {
            server.run();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.close();
        }
    }
}
