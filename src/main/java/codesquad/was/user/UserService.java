package codesquad.was.user;

import codesquad.was.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static codesquad.was.repository.UserRepository.*;

public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public void save(User user) {
        if(user.getUserId().isBlank()){
            logger.debug("유저아이디는 필수 값 입니다.");
            return;
        }
        if(!isExistUser(user.getUserId())){
            //todo : 예외처리로 변경
            userRepository.save(user.getUserId(),user);
            return;
        }
        logger.debug("이미 저장 된 유저입니다");
    }

    public User getUserByIdAndPw(String userId,String password) throws BadRequestException {
        User user = userRepository.findById(userId);
        if(user == null){
            return null;
        }
        if(user.getPassword().equals(password)){
            return user;
        }
        return null;
    }

    private static boolean isExistUser(String userId) {
        return userRepository.isExists(userId);
    }

    public User get(String userId) throws BadRequestException {
        return userRepository.findById(userId);
    }


}
