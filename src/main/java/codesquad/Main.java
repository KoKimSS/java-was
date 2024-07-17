package codesquad;

import codesquad.was.server.Server;
import java.io.IOException;
import java.sql.SQLException;



public class Main {

    public static void main(String[] args) throws IOException{
        Server server = new Server(10,8080, 10);

        try {
            server.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
