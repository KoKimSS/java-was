package codesquad.was.user;

import codesquad.was.exception.BadRequestException;
import codesquad.was.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static codesquad.was.repository.UserRepository.*;


class UserServiceTest {

    private final UserService userService = new UserService();

    @Test
    void save() {
        //given
        String id = "seung123";
        String name = "seungsu";
        String password = "994499";
        User user = User.factoryMethod(id, name, password);

        //when
        userService.save(user);

        //then
        Assertions.assertThat(userRepository.getSize()).isEqualTo(1);
    }

    @Test
    void get() throws BadRequestException {
        //given
        String id = "seung123";
        String name = "seungsu";
        String password = "994499";
        User user = User.factoryMethod(id, name, password);

        //when
        userService.save(user);
        User savedUser = userService.get("seung123");

        //then
        Assertions.assertThat(user).isEqualTo(savedUser);
    }
}