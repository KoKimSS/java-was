package codesquad;

import codesquad.was.server.Server;
import codesquad.was.user.User;

import java.io.IOException;

import static codesquad.was.repository.UserRepository.*;
import static codesquad.was.server.ServerConfig.*;
import static codesquad.was.server.ServerConfig.THREAD_POOL_SIZE;


public class Main {

    public static void main(String[] args) throws IOException {
        userRepository.save("seungsu",User.factoryMethod("seungsu","승수","123123"));
        userRepository.save("seungsu2",User.factoryMethod("seungsu2","승수2","123123"));
        userRepository.save("seungsu3",User.factoryMethod("seungsu3","승수3","123123"));
        userRepository.save("seungsu4",User.factoryMethod("seungsu4","승수4","123123"));
        Server server = new Server(THREAD_POOL_SIZE,PORT, BACKLOG);
        try {
            server.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
