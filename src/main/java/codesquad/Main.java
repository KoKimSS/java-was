package codesquad;

import codesquad.business.domain.User;
import codesquad.business.repository.UserRepository;
import codesquad.was.server.Server;
import java.io.IOException;

import static codesquad.business.configuration.UrlPathResourceMapConfig.setUrlPathResourceMap;
import static codesquad.business.configuration.handlerMapConfig.setHandlerMap;


public class Main {

    public static void main(String[] args) throws IOException {
        Server server = new Server(10,8080, 10);
        setUrlPathResourceMap();
        setHandlerMap();
        UserRepository userRepository = UserRepository.userRepository;
        userRepository.save("seungsu",User.factoryMethod("seungsu","승수","123123"));
        try {
            server.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
