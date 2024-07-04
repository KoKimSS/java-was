package codesquad.http.user;

import codesquad.http.log.Log;
import codesquad.http.repository.MemoryRepository;

import static codesquad.http.repository.MemoryRepository.*;

public class UserRepository {

    public void save(User user) {
        if(user.getUserId().isBlank()){
            Log.log("유저아이디는 필수 값 입니다.");
            return;
        }
        if(!isExistUser(user.getUserId())){
            //todo : 예외처리로 변경
            put("User"+user.getUserId(),user);
            printMap();
            return;
        }
        Log.log("이미 저장 된 유저입니다");
    }

    private static boolean isExistUser(String userId) {
        return containsKey("User" + userId);
    }

    public User get(String userId) {
        return (User) MemoryRepository.get("User"+userId).orElseThrow(IllegalArgumentException::new);
    }


}
