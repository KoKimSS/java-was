package codesquad;

import codesquad.was.repository.MemoryRepository;
import codesquad.was.server.Server;
import codesquad.was.user.User;
import com.sun.source.tree.MemberReferenceTree;

import java.io.IOException;

import static codesquad.was.server.ServerConfig.*;
import static codesquad.was.server.ServerConfig.THREAD_POOL_SIZE;


public class Main {

    public static void main(String[] args) throws IOException {
        MemoryRepository.put("Userseungsu", User.factoryMethod("seungsu","승수","123123"));
        Server server = new Server(THREAD_POOL_SIZE,PORT, BACKLOG);
        try {
            server.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
