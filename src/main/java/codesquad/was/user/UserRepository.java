package codesquad.was.user;

import codesquad.was.exception.BadRequestException;
import codesquad.was.log.Log;
import codesquad.was.repository.MemoryRepository;

import static codesquad.was.repository.MemoryRepository.*;

public class UserRepository {

    public void save(User user) {
        if(user.getUserId().isBlank()){
            Log.log("유저아이디는 필수 값 입니다.");
            return;
        }
        if(!isExistUser(user.getUserId())){
            //todo : 예외처리로 변경
            put("User"+user.getUserId(),user);
            return;
        }
        Log.log("이미 저장 된 유저입니다");
    }

    public User getUserByIdAndPw(String userId,String password) throws BadRequestException {
        User user = get(userId);
        if(user == null){
            return null;
        }
        if(user.getPassword().equals(password)){
            return user;
        }
        return null;
    }

    private static boolean isExistUser(String userId) {
        return containsKey("User" + userId);
    }

    public User get(String userId) throws BadRequestException {
        System.out.println("유저아이디"+userId);
        Object o = MemoryRepository.get("User" + userId);
        return (User) o;
    }


}
