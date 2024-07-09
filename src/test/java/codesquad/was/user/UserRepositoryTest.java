package codesquad.was.user;

import codesquad.was.exception.BadRequestException;
import codesquad.was.repository.MemoryRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


class UserRepositoryTest {

    private final UserRepository userRepository  = new UserRepository();

    @Test
    void save() {
        //given
        String id = "seung123";
        String name = "seungsu";
        String password = "994499";
        User user = User.factoryMethod(id, name, password);

        //when
        userRepository.save(user);

        //then
        Assertions.assertThat(MemoryRepository.getSize()).isEqualTo(1);
    }

    @Test
    void get() throws BadRequestException {
        //given
        String id = "seung123";
        String name = "seungsu";
        String password = "994499";
        User user = User.factoryMethod(id, name, password);

        //when
        userRepository.save(user);
        User savedUser = userRepository.get("seung123");

        //then
        Assertions.assertThat(user).isEqualTo(savedUser);
    }
}